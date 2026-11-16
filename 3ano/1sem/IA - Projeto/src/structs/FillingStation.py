"""FillingStation class for charging and refueling stations."""
from dataclasses import dataclass
from structs.enums import StationType, VehicleType

@dataclass
class FillingStation:
    """Represents a charging station and gas station."""

    id: str
    node_id: int

    max_capacity: int = 5
    current_occupancy: int = 0

    type: StationType = StationType.MIXED

    charging_rate_kwh_per_hour: float = 7.0
    refuel_time_minutes: float = 5.0

    active: bool = True

    def is_available(self) -> bool:
        """Check if station has capacity and is active."""
        return self.active and self.current_occupancy < self.max_capacity

    def supports_vehicle(self, vehicle_type: VehicleType) -> bool:
        """Check if station supports the given vehicle type."""

        if self.type == StationType.MIXED:
            return True

        if self.type == StationType.ELECTRIC:
             return vehicle_type in [VehicleType.EBUS, VehicleType.ECO]

        if self.type == StationType.COMBUSTION:
             return vehicle_type in [VehicleType.GASOLINE, VehicleType.DIESEL]

        return False

    def add_vehicle(self) -> bool:
        """Add a vehicle to the station."""
        if self.current_occupancy < self.max_capacity:
            self.current_occupancy += 1
            return True
        return False

    def remove_vehicle(self):
        """Remove a vehicle from the station."""
        if self.current_occupancy > 0:
            self.current_occupancy -= 1
