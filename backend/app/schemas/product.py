# file: backend/app/schemas/product.py
from pydantic import BaseModel
from typing import Optional, List

class CategoryResponse(BaseModel):
    id: str
    name: str
    sort_order: int

    class Config:
        from_attributes = True

class ProductResponse(BaseModel):
    id: str
    category_id: str
    name: str
    description: Optional[str] = None
    price: float
    image_url: Optional[str] = None
    is_available: bool
    is_spicy: bool
    is_vegetarian: bool

    class Config:
        from_attributes = True

class MenuResponse(BaseModel):
    categories: List[CategoryResponse]
    products: List[ProductResponse]