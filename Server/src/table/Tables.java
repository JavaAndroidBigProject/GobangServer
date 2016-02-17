package table;

import java.util.LinkedList;

public class Tables {
    private final int DEFAULT_NUM = 4;
    private int tableNum;

    private LinkedList<Table> tables = null;

    public Tables(int num){
        if(num <= 0)
            num = DEFAULT_NUM;
        this.tableNum = num;
        tables = new LinkedList<>();
        for(int i = 1; i <= num; i++){
            tables.add(new Table(i));
        }
    }

    public Table getTable(int tableId){
        for(Table table : tables){
            if(table.getId() == tableId)
                return table;
        }
        return null;
    }

    public TableInfo[] getTableInfos(){
        TableInfo[] infos = new TableInfo[tableNum];
        for(Table t : tables){
            infos[t.getId() - 1] = t.getTableInfo();
        }
        return infos;
    }
}
