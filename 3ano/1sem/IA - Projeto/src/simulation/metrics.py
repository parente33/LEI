"""Metrics Collector."""

class Metrics:
    """Singleton-like class to track simulation metrics."""

    _instance = None

    def __new__(cls):
        if cls._instance is None:
            cls._instance = super(Metrics, cls).__new__(cls)
            cls._instance.reset()
        return cls._instance

    def reset(self):
        self.total_co2_kg = 0.0
        self.total_cost_eur = 0.0
        self.total_distance_km = 0.0
        self.total_occupied_km = 0.0
        self.total_empty_km = 0.0
        self.completed_requests = 0
        self.avg_wait_time = 0.0
        self._wait_time_sum = 0.0

    def add_movement(self, distance_km: float, is_occupied: bool, emissions_g: float, cost_eur: float):
        """Register vehicle movement statistics."""
        self.total_distance_km += distance_km
        if is_occupied:
            self.total_occupied_km += distance_km
        else:
            self.total_empty_km += distance_km

        self.total_co2_kg += (emissions_g * distance_km) / 1000.0
        self.total_cost_eur += (cost_eur * distance_km)

    def complete_request(self, wait_time_min: float):
        """Register request completion."""
        self.completed_requests += 1
        self._wait_time_sum += wait_time_min
        self.avg_wait_time = self._wait_time_sum / self.completed_requests

    def get_report(self):
        return {
            "CO2 (kg)": f"{self.total_co2_kg:.2f}",
            "Cost (€)": f"{self.total_cost_eur:.2f}",
            "Dist (km)": f"{self.total_distance_km:.2f}",
            "Occupancy (%)": f"{((self.total_occupied_km / self.total_distance_km)*100) if self.total_distance_km > 0 else 0:.1f}",
            "Avg Wait (m)": f"{self.avg_wait_time:.1f}"
        }
