# file: backend/app/models/address.py
import uuid
from sqlalchemy import String, ForeignKey, Boolean, Float
from sqlalchemy.orm import Mapped, mapped_column, relationship
from app.core.database import Base

class Address(Base):
    __tablename__ = "addresses"

    id: Mapped[str] = mapped_column(String, primary_key=True, default=lambda: str(uuid.uuid4()))
    user_id: Mapped[str] = mapped_column(String, ForeignKey("users.id", ondelete="CASCADE"))
    city: Mapped[str] = mapped_column(String)
    street: Mapped[str] = mapped_column(String)
    house: Mapped[str] = mapped_column(String)
    apartment: Mapped[str] = mapped_column(String, nullable=True)
    entrance: Mapped[str] = mapped_column(String, nullable=True)
    floor: Mapped[str] = mapped_column(String, nullable=True)
    intercom: Mapped[str] = mapped_column(String, nullable=True)
    lat: Mapped[float] = mapped_column(Float, nullable=True)
    lng: Mapped[float] = mapped_column(Float, nullable=True)
    is_default: Mapped[bool] = mapped_column(Boolean, default=False)

    user = relationship("User", back_populates="addresses")