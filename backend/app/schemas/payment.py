# file: backend/app/schemas/payment.py
from pydantic import BaseModel

class PaymentCreateRequest(BaseModel):
    order_id: str
    amount: float
    return_url: str

class PaymentResponse(BaseModel):
    id: str
    status: str
    confirmation_url: str