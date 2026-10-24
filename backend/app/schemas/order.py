# file: backend/app/schemas/order.py
from pydantic import BaseModel
from typing import Optional, List
from datetime import datetime

class OrderItemRequest(BaseModel):
    product_id: str
    quantity: int

class OrderCreateRequest(BaseModel):
    address_id: Optional[str] = None
    items: List[OrderItemRequest]
    note: Optional[str] = None
    delivery_time: Optional[datetime] = None
    payment_method: str = "SBP"

class OrderItemResponse(BaseModel):
    id: str
    product_id: str
    name: str
    quantity: int
    price: float

    class Config:
        from_attributes = True

class OrderResponse(BaseModel):
    id: str
    status: str
    total_price: float
    delivery_price: float
    discount: float
    note: Optional[str] = None
    created_at: datetime
    delivery_time: Optional[datetime] = None
    items: List[OrderItemResponse]

    class Config:
        from_attributes = True