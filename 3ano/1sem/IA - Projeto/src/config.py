"""Simulation Configuration."""
from structs.enums import VehicleType

# Simulation Parameters
SIM_CONFIG = {
    "start_time_minutes": 480.0, # 08:00 AM
    "step_size_minutes": 0.0225,
}

# Vehicle Specifications
VEHICLE_SPECS = {
    VehicleType.EBUS: {
        "max_energy": 300.0,     # kWh
        "consumption": 1.2,      # kWh/km
        "emissions": 0.0,        # gCO2/km
        "cost_per_km": 0.15,     # €/km (Maintenance + Energy)
        "capacity": 20,
        "refuel_time": 45.0      # min
    },
    VehicleType.ECO: {
        "max_energy": 50.0,      # kWh
        "consumption": 0.15,     # kWh/km
        "emissions": 0.0,
        "cost_per_km": 0.10,
        "capacity": 4,
        "refuel_time": 30.0
    },
    VehicleType.GASOLINE: {
        "max_energy": 50.0,      # Liters
        "consumption": 0.08,     # L/km (~8L/100km)
        "emissions": 120.0,      # gCO2/km
        "cost_per_km": 0.14,     # €/km (Fuel @ 1.7€/L)
        "capacity": 4,
        "refuel_time": 5.0       # Fast
    },
    VehicleType.DIESEL: {
        "max_energy": 60.0,      # Liters
        "consumption": 0.06,     # L/km (~6L/100km)
        "emissions": 110.0,      # gCO2/km
        "cost_per_km": 0.11,     # €/km
        "capacity": 4,
        "refuel_time": 5.0
    }
}

# Algorithm Weights & Heuristic Constants
ALGORITHM_CONFIG = {
    "weight_time": 1.0,
    "weight_cost": 10.0,
    "weight_co2": 0.5,
    "max_speed_reference": 120.0,
    "default_cost_per_km": 0.1,
    "default_emissions": 0.05
}
