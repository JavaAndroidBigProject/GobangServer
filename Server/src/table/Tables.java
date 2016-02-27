package table;

import java.util.LinkedList;

public class Tables {
    private final int DEFAULT_NUM = 4;
    private int tableNum;

    private LinkedList<String> loginedPlayer = new LinkedList<>();           //已登录玩家名字
    private LinkedList<Table> tables = new LinkedList<>();

    public Tables(int num){
        if(num <= 0)
            num = DEFAULT_NUM;
        this.tableNum = num;

        for(int i = 1; i <= num; i++){
            tables.add(new Table(i));
        }
    }

    public void addLoginedPlayer(String userName){               //添加已登录用户名
        loginedPlayer.add(userName);
    }

    public void removeLoginedPlayer(String userName){            //移除已登录用户名
        for(String s : loginedPlayer){
            if(s.equals(userName))
                loginedPlayer.remove(s);
        }
    }

    public boolean isPlayerLogined(String userName){             //判断用户名是否已登录
        for(String s : loginedPlayer){
            if(s.equals(userName))
                return true;
        }
        return false;
    }

    public Table getTable(int tableId){                         //获取ID为tableId的桌子
        for(Table table : tables){
            if(table.getId() == tableId)
                return table;
        }
        return null;
    }

    public TableInfo[] getTableInfos(){                         //获取桌子信息数组
        TableInfo[] infos = new TableInfo[tableNum];
        for(Table t : tables){
            infos[t.getId() - 1] = t.getTableInfo();
        }
        return infos;
    }
}
