# file: backend/app/routers/menu.py
from fastapi import APIRouter, Depends
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.future import select
from app.core.database import get_db
from app.models.category import Category
from app.models.product import Product
from app.schemas.product import MenuResponse, CategoryResponse, ProductResponse

router = APIRouter()

@router.get("", response_model=MenuResponse)
async def get_menu(db: AsyncSession = Depends(get_db)):
    cat_result = await db.execute(select(Category).order_by(Category.sort_order))
    categories = cat_result.scalars().all()
    
    prod_result = await db.execute(select(Product))
    products = prod_result.scalars().all()
    
    return MenuResponse(
        categories=[CategoryResponse.model_validate(c) for c in categories],
        products=[ProductResponse.model_validate(p) for p in products]
    )