# file: backend/app/routers/payment.py
from fastapi import APIRouter, HTTPException
from app.schemas.payment import PaymentCreateRequest, PaymentResponse
from app.services.yookassa import create_yookassa_payment

router = APIRouter()

@router.post("", response_model=PaymentResponse)
async def create_payment(payment_in: PaymentCreateRequest):
    try:
        payment_data = await create_yookassa_payment(
            amount=payment_in.amount,
            order_id=payment_in.order_id,
            return_url=payment_in.return_url
        )
        return PaymentResponse(
            id=payment_data["id"],
            status=payment_data["status"],
            confirmation_url=payment_data["confirmation"]["confirmation_url"]
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))