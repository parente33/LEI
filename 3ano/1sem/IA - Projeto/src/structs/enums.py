"""Enumerations for vehicle and request statuses."""

from enum import Enum


class VehicleStatus(Enum):
    """Status of a vehicle in the fleet."""
    AVAILABLE           = "available"
    EN_ROUTE_TO_PICKUP  = "en_route_to_pickup"
    OCCUPIED            = "occupied"
    EN_ROUTE_TO_STATION = "en_route_to_station"
    CHARGING_REFUELING  = "charging_refueling"
    UNAVAILABLE         = "unavailable"


class RequestStatus(Enum):
    """Status of a transportation request."""
    PENDING   = "pending"
    ASSIGNED  = "assigned"
    PICKED_UP = "picked_up"
    COMPLETED = "completed"
    REJECTED  = "rejected"
    EXPIRED   = "expired"


class VehicleType(Enum):
    """Type of vehicle engine."""
    EBUS       = "ebus"
    ECO        = "eco"
    GASOLINE   = "gasoline"
    DIESEL     = "diesel"


class StationType(Enum):
    """Type of charging/refueling station."""
    ELECTRIC   = "electric"
    COMBUSTION = "combustion"
    MIXED      = "mixed"
