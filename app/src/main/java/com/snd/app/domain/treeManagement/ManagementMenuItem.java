package com.snd.app.domain.treeManagement;

import lombok.Data;

@Data
public class ManagementMenuItem {
    private String menuName;

    public ManagementMenuItem(String menuName) {
        this.menuName = menuName;
    }
}
