"""Request struct - Data Holder."""
from dataclasses import dataclass
from typing import Optional
from structs.enums import RequestStatus

@dataclass
class Request:
    """Represents a client request."""
    id: str
    origin_node: int
    destination_node: int
    created_time: float

    status: RequestStatus = RequestStatus.PENDING
    priority: int = 1
    max_wait_time: float = 15.0
    env_preference: bool = False

    # Assignment info
    assigned_vehicle_id: Optional[str] = None
    pickup_time: Optional[float] = None
    dropoff_time: Optional[float] = None
