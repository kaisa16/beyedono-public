# file: backend/app/models/order.py
import uuid
from datetime import datetime
from sqlalchemy import String, ForeignKey, Float, DateTime, Text
from sqlalchemy.orm import Mapped, mapped_column, relationship
from app.core.database import Base

class Order(Base):
    __tablename__ = "orders"

    id: Mapped[str] = mapped_column(String, primary_key=True, default=lambda: str(uuid.uuid4()))
    user_id: Mapped[str] = mapped_column(String, ForeignKey("users.id"))
    address_id: Mapped[str] = mapped_column(String, nullable=True)
    status: Mapped[str] = mapped_column(String, default="PENDING")
    total_price: Mapped[float] = mapped_column(Float)
    delivery_price: Mapped[float] = mapped_column(Float, default=0.0)
    discount: Mapped[float] = mapped_column(Float, default=0.0)
    note: Mapped[str] = mapped_column(Text, nullable=True)
    payment_method: Mapped[str] = mapped_column(String, default="SBP")
    created_at: Mapped[datetime] = mapped_column(DateTime, default=datetime.utcnow)
    delivery_time: Mapped[datetime] = mapped_column(DateTime, nullable=True)

    user = relationship("User", back_populates="orders")
    items = relationship("OrderItem", back_populates="order", cascade="all, delete-orphan")