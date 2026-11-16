"""State container for the simulation."""
from dataclasses import dataclass, field
from typing import List, Any

from structs.Vehicle import Vehicle
from structs.Request import Request
from structs.FillingStation import FillingStation
from structs.TaxiStand import TaxiStand

@dataclass
class State:
    """Represents the simulation state."""

    graph: Any
    vehicles: List[Vehicle]

    stations: List[FillingStation]
    taxi_stands: List[TaxiStand]

    requests: List[Request] = field(default_factory=list)

    current_time: float = 0.0
    traffic_manager: Any = None
