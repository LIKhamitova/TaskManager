package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StatisticDto {
    private String totalUsers;
    private String totalTasks;
    private String plannedTasks;
    private String inProcessTasks;
    private String completedTasks;
    private String cancelledTasks;
    private String deletedTasks;
    private String lowPriorityTasks;
    private String mediumPriorityTasks;
    private String highPriorityTasks;
    private String totalGroups;

    public void setTotalUsers(Object users) {
        this.totalUsers = users != null ? users.toString() : "0";
    }

    public void setTotalTasks(Object tasks) {
        this.totalTasks = tasks != null ? tasks.toString() : "0";
    }

    public void setInProcessTasks(Object inProcessTasks) {
        this.inProcessTasks = inProcessTasks != null ? inProcessTasks.toString() : "0";
    }

    public void setPlannedTasks(Object plannedTasks) {
        this.plannedTasks = plannedTasks != null ? plannedTasks.toString() : "0";
    }

    public void setCompletedTasks(Object completedTasks) {
        this.completedTasks = completedTasks != null ? completedTasks.toString() : "0";
    }

    public void setCancelledTasks(Object cancelledTasks) {
        this.cancelledTasks = cancelledTasks != null ? cancelledTasks.toString() : "0";
    }

    public void setDeletedTasks (Object deletedTasks ) {
        this.deletedTasks = deletedTasks != null ? deletedTasks.toString() : "0";
    }

    public void setLowPriorityTasks (Object lowPriorityTasks) {
        this.lowPriorityTasks = lowPriorityTasks != null ? lowPriorityTasks.toString() : "0";
    }

    public void setMediumPriorityTasks (Object mediumPriorityTasks) {
        this.mediumPriorityTasks = mediumPriorityTasks != null ? mediumPriorityTasks.toString() : "0";;
    }

    public void setHighPriorityTasks (Object highPriorityTasks) {
        this.highPriorityTasks = highPriorityTasks != null ? highPriorityTasks.toString() : "0";
    }

    public void setTotalGroups (Object totalGroups) {
        this.totalGroups = totalGroups != null ? totalGroups.toString() : "0";
    }

}
