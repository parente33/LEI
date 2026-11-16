package uminho.dss.ui.controller;
import uminho.dss.ui.view.*;
import uminho.dss.ui.UIException;

import uminho.dss.business.BusinessException;
import uminho.dss.business.IBusiness;
import uminho.dss.business.kitchen.TaskInfo;

import java.util.List;

public class EmployeeController {

    private IBusiness model;
    private EmployeeView view;
    private String employeeName;
    private String workstationCode;
    private int restaurantId;

    public EmployeeController(IBusiness model, String employeeName, String workstationCode, int restaurantId) {
        this.model = model;
        this.view = new EmployeeView();
        this.employeeName = employeeName;
        this.workstationCode = workstationCode;
        this.restaurantId = restaurantId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void handleViewTasks() throws UIException {
        try {
            List<TaskInfo> tasks = model.getWorkstationTasks(workstationCode);
            view.displayTasks(tasks, workstationCode);
        } catch (BusinessException e) {
            throw new UIException(e.getMessage());
        }
    }

    public void handleStartTask() throws UIException {
        try {
            List<TaskInfo> tasks = model.getWorkstationTasks(workstationCode);
            List<TaskInfo> pendingTasks = tasks.stream()
                    .filter(t -> t.getStatus().equals("PENDING") || t.getStatus().equals("DELAYED"))
                    .toList();

            if (pendingTasks.isEmpty()) {
                view.showError("Sem tarefas pendentes para iniciar.");
                return;
            }

            boolean hasInProgress = tasks.stream().anyMatch(t -> t.getStatus().equals("IN_PROGRESS"));

            if (hasInProgress) {
                view.displayAlreadyInProgress();
                return;
            }

            Integer taskId = view.promptTaskSelection(pendingTasks, "Selecionar Tarefa para Iniciar");
            if (taskId == null)
                return;

            model.startTask(taskId);
            view.displayTaskStarted(taskId);

        } catch (BusinessException e) {
            throw new UIException(e.getMessage());
        }
    }

    public void handleCompleteTask() throws UIException {
        try {
            List<TaskInfo> tasks = model.getWorkstationTasks(workstationCode);
            TaskInfo inProgressTask = tasks.stream()
                    .filter(t -> t.getStatus().equals("IN_PROGRESS"))
                    .findFirst()
                    .orElse(null);

            if (inProgressTask == null) {
                view.displayNoTaskInProgress();
                return;
            }

            if (view.confirmTaskCompletion(inProgressTask)) {
                model.completeTask(inProgressTask.getTaskId());
                view.displayTaskCompleted(inProgressTask.getTaskId());
            } else {
                view.displayOperationCancelled();
            }

        } catch (BusinessException e) {
            throw new UIException(e.getMessage());
        }
    }

    public void handleDelayTask() throws UIException {
        try {
            List<TaskInfo> tasks = model.getWorkstationTasks(workstationCode);
            TaskInfo inProgressTask = tasks.stream()
                    .filter(t -> t.getStatus().equals("IN_PROGRESS"))
                    .findFirst()
                    .orElse(null);

            if (inProgressTask == null) {
                view.displayNoTaskInProgress();
                return;
            }

            EmployeeView.DelayData data = view.promptTaskDelay(inProgressTask);
            model.delayTask(inProgressTask.getTaskId(), data.delayMinutes, data.reason);
            view.displayTaskDelayed(inProgressTask.getTaskId(), data.delayMinutes);

        } catch (BusinessException e) {
            throw new UIException(e.getMessage());
        }
    }

    public void handleRequestIngredient() throws UIException {
        try {
            EmployeeView.IngredientRequest request = view.promptIngredientRequest();
            model.requestIngredient(restaurantId, request.ingredientName, request.quantity);
            view.displayIngredientRequested(request.ingredientName, request.quantity);
        } catch (NumberFormatException e) {
            throw new UIException("Quantidade invalida.");
        } catch (BusinessException e) {
            throw new UIException(e.getMessage());
        }
    }

    public void handleViewMessages() throws UIException {
        try {
            var messages = model.getRecentMessages(10);
            view.displayMessages(messages);
        } catch (BusinessException e) {
            throw new UIException(e.getMessage());
        }
    }
}
