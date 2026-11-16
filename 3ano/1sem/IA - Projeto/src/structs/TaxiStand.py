"""TaxiStand class for vehicle waiting areas."""
from dataclasses import dataclass, field
from typing import List


@dataclass
class TaxiStand:
    """Represents a taxi stand where vehicles can wait and requests can originate."""

    id: str
    node_id: int
    max_capacity: int = 5

    # Current vehicles at the stand (IDs)
    vehicles: List[str] = field(default_factory=list)

    def add_vehicle(self, vehicle_id: str) -> bool:
        """Add a vehicle to the stand."""
        if len(self.vehicles) < self.max_capacity:
            self.vehicles.append(vehicle_id)
            return True
        return False

    def remove_vehicle(self, vehicle_id: str) -> bool:
        """Remove a vehicle from the stand."""
        if vehicle_id in self.vehicles:
            self.vehicles.remove(vehicle_id)
            return True
        return False

    def is_full(self) -> bool:
        """Check if stand is full."""
        return len(self.vehicles) >= self.max_capacity
