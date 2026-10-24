# file: backend/app/routers/orders.py
from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.future import select
from sqlalchemy.orm import selectinload
from typing import List
from app.core.database import get_db
from app.models.order import Order
from app.models.order_item import OrderItem
from app.models.product import Product
from app.schemas.order import OrderCreateRequest, OrderResponse, OrderItemResponse

router = APIRouter()

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

    order.total_price = total_price
    await db.commit()
    await db.refresh(order)
    
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