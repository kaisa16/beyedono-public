# file: backend/app/routers/orders.py
from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.future import select
from sqlalchemy.orm import selectinload
from typing import List
import httpx
import os
from datetime import datetime
from app.core.database import get_db
from app.models.order import Order
from app.models.order_item import OrderItem
from app.models.product import Product
from app.schemas.order import OrderCreateRequest, OrderResponse, OrderItemResponse

router = APIRouter()

async def send_order_to_frontpad(order_data: dict):
    """Отправляет заказ в Frontpad API"""
    secret = os.getenv("FRONTPAD_SECRET")
    url = os.getenv("FRONTPAD_API_URL")
    if not secret or not url:
        print("Frontpad secret or URL is missing in .env")
        return

    # Формируем данные для Frontpad
    payload = {"secret": secret, **order_data}
    
    async with httpx.AsyncClient() as client:
        try:
            response = await client.post(url, data=payload)
            response.raise_for_status()
            res_json = response.json()
            if res_json.get("result") == "success":
                print(f"Заказ успешно отправлен в Frontpad. ID: {res_json.get('order_id')}")
            else:
                print(f"Ошибка Frontpad: {res_json.get('error')}")
        except Exception as e:
            print(f"Ошибка сети при отправке в Frontpad: {e}")

@router.post("", response_model=OrderResponse)
async def create_order(order_in: OrderCreateRequest, db: AsyncSession = Depends(get_db)):
    total_price = 0.0
    order_items = []
    
    order = Order(
        user_id="guest_user_id",
        address_id=order_in.address_id,
        status="PENDING",
        note=order_in.note,
        payment_method=order_in.payment_method,
        delivery_price=0.0,
        discount=0.0
    )
    db.add(order)
    await db.flush()

    # Данные для Frontpad
    fp_products = []
    fp_quantities = []
    fp_prices = []

    for item in order_in.items:
        prod_res = await db.execute(select(Product).where(Product.id == item.product_id))
        product = prod_res.scalars().first()
        if not product:
            raise HTTPException(status_code=404, detail=f"Product {item.product_id} not found")

        item_total = product.price * item.quantity
        total_price += item_total

        oi = OrderItem(
            order_id=order.id,
            product_id=product.id,
            quantity=item.quantity,
            price=product.price
        )
        db.add(oi)
        order_items.append(oi)

        # Собираем данные для Frontpad
        # ВАЖНО: здесь product.id должен быть числовым артикулом из Frontpad.
        # Если у тебя строковый ID, используй поле product.sku или другое поле.
        fp_products.append(str(product.id))
        fp_quantities.append(str(item.quantity))
        fp_prices.append(str(product.price))

    order.total_price = total_price
    await db.commit()
    await db.refresh(order)

    # Подготовка к отправке в Frontpad
    # ВАЖНО: замени 'street' на реальное поле из твоего order_in (например, order_in.address_id)
    frontpad_payload = {
        "product": fp_products,
        "product_kol": fp_quantities,
        "product_price": fp_prices,
        "phone": "79000000000", # Вставь сюда телефон пользователя
        "street": "Тестовая",   # Вставь сюда адрес
        "descr": order.note or "",
        "datetime": datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    }
    
    # Отправляем в фоне (не блокируя ответ пользователю)
    await send_order_to_frontpad(frontpad_payload)

    return OrderResponse(
        id=order.id,
        status=order.status,
        total_price=order.total_price,
        delivery_price=order.delivery_price,
        discount=order.discount,
        note=order.note,
        created_at=order.created_at,
        delivery_time=order.delivery_time,
        items=[
            OrderItemResponse(
                id=oi.id,
                product_id=oi.product_id,
                name="Product",
                quantity=oi.quantity,
                price=oi.price
            ) for oi in order_items
        ]
    )

@router.get("", response_model=List[OrderResponse])
async def get_orders(db: AsyncSession = Depends(get_db)):
    result = await db.execute(
        select(Order).options(selectinload(Order.items).selectinload(OrderItem.product)).order_by(Order.created_at.desc())
    )
    orders = result.scalars().all()
    return [
        OrderResponse(
            id=o.id,
            status=o.status,
            total_price=o.total_price,
            delivery_price=o.delivery_price,
            discount=o.discount,
            note=o.note,
            created_at=o.created_at,
            delivery_time=o.delivery_time,
            items=[
                OrderItemResponse(
                    id=oi.id,
                    product_id=oi.product_id,
                    name=oi.product.name if oi.product else "Unknown",
                    quantity=oi.quantity,
                    price=oi.price
                ) for oi in o.items
            ]
        ) for o in orders
    ]