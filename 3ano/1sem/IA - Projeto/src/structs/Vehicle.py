"""Vehicle struct - Data Holder."""
from dataclasses import dataclass
from typing import List, Optional
from structs.enums import VehicleType, VehicleStatus

@dataclass
class Vehicle:
    """Represents a vehicle in the fleet."""
    id: str
    type: VehicleType

    # Position & Movement
    current_node: int
    path: List[int]
    path_index: int

    # State
    status: VehicleStatus
    capacity: int = 4

    # Energy
    max_energy: float = 100.0   # kWh or Liters
    current_energy: float = 100.0

    # Assignment
    assigned_request_id: Optional[str] = None

    # Internal Physics
    dist_traveled_on_edge: float = 0.0
    remaining_refuel_time: float = 0.0

    def has_path(self) -> bool:
        return len(self.path) > 1 and self.path_index < len(self.path) - 1