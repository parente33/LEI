from interfaces import IView
from state import State
from structs.enums import VehicleStatus, RequestStatus

class ConsoleView(IView):
    def __init__(self, update_interval=120):
        self.update_interval = update_interval
        self.step_count      = 0

    def update(self, state: State):
        self.step_count += 1

        if self.step_count % self.update_interval == 0:
            self._print_status(state)

    def _print_status(self, state: State):
        mins = int(state.current_time)

        hh  = (mins // 60) % 24
        mm  = mins % 60
        day = (mins // 1440) + 1

        # Vehicle Stats
        free_v = sum(v.status == VehicleStatus.AVAILABLE for v in state.vehicles)
        busy_v = sum(v.status in (
            VehicleStatus.OCCUPIED,
            VehicleStatus.EN_ROUTE_TO_PICKUP
        ) for v in state.vehicles)
        chrg_v = sum(v.status in (
            VehicleStatus.EN_ROUTE_TO_STATION,
            VehicleStatus.CHARGING_REFUELING
        ) for v in state.vehicles)

        # Request Stats
        pending_r   = sum(r.status == RequestStatus.PENDING for r in state.requests)
        completed_r = sum(r.status == RequestStatus.COMPLETED for r in state.requests)

        # Metrics
        m_str = "N/A"
        if hasattr(state, "metrics"):
            rep = state.metrics.get_report()
            m_str = (
                f"Wait:{rep['Avg Wait (m)']:>4}m | "
                f"CO2:{rep['CO2 (kg)']:>5}kg | "
                f"Cost:{rep['Cost (€)']:>5}€"
            )

        W = 56
        header = f"Day {day} {hh:02d}:{mm:02d}"

        print("\n┌── [" + header + "] " + "─"*(W - len(header) - 8) + "┐")
        print(f"│ Fleet    Free:{free_v:>3} | Busy:{busy_v:>3} | Charging:{chrg_v:>3}"
            .ljust(W-1) + "│")
        print(f"│ Requests Pending:{pending_r:>3} | Done:{completed_r:>3}"
            .ljust(W-1) + "│")
        print("│" + "─"*(W-2) + "│")
        print(f"│ Perf  {m_str}".ljust(W-1) + "│")
        print("└" + "─"*(W-2) + "┘")