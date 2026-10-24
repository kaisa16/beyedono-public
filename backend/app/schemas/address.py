# file: backend/app/schemas/address.py
from pydantic import BaseModel
from typing import Optional

class AddressBase(BaseModel):
    city: str
    street: str
    house: str
    apartment: Optional[str] = None
    entrance: Optional[str] = None
    floor: Optional[str] = None
    intercom: Optional[str] = None
    lat: Optional[float] = None
    lng: Optional[float] = None
    is_default: bool = False

class AddressCreate(AddressBase):
    pass

class AddressResponse(AddressBase):
    id: str
    user_id: str

    class Config:
        from_attributes = True