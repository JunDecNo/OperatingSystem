import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class DiskScheduling implements ActionListener {
    private JFrame jFrame;
    private JTextArea jTextArea;
    private JButton SSTF,SCAN,CSCAN,New;
    private JScrollPane jScrollPane,jscrollpane;
    private JTable jTable;
    private JPanel jPanel;
    private DefaultTableModel model;
    private String input[];
    private int array[],count,start,sum=0;
    DiskScheduling(){
        String column[]={"被访问的下一个磁道号","移动距离(磁道数)"};
        model=new DefaultTableModel(column,0);
        New=new JButton("创建");
        SSTF=new JButton("最短寻道时间优先SSTF");
        SCAN=new JButton("扫描算法SCAN");
        CSCAN=new JButton("循环扫描算法CSCAN");
        jTable=new JTable(model);
        jFrame=new JFrame("磁盘调度算法");
        jPanel=new JPanel();
        jTextArea=new JTextArea(10,50);
        jScrollPane=new JScrollPane(jTable);
        jscrollpane=new JScrollPane(jTextArea);
        Setting();
        Add();
    }
    void Setting(){
        DefaultTableCellRenderer r = new DefaultTableCellRenderer();
        r.setHorizontalAlignment(JLabel.CENTER);
        jFrame.setLayout(new BorderLayout());
        jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        jFrame.setBounds(100,100,1000,600);
        jTextArea.setDisabledTextColor(Color.BLACK);
        jTextArea.setWrapStyleWord(true);
        jTable.setDefaultRenderer(Object.class,r);
        jTable.setRowHeight(30);
        jTextArea.setFont(new Font("黑体",Font.PLAIN,16));
        jFrame.setVisible(true);
    }
    void Add(){
        jFrame.add(jscrollpane,BorderLayout.NORTH);
        jPanel.add(New);jPanel.add(SSTF);jPanel.add(SCAN);jPanel.add(CSCAN);
        New.addActionListener(this);
        SSTF.addActionListener(this);
        SCAN.addActionListener(this);
        CSCAN.addActionListener(this);
        jFrame.add(jScrollPane,BorderLayout.CENTER);
        jFrame.add(jPanel,BorderLayout.SOUTH);
    }
    public static void main(String args[]){
        new DiskScheduling();
    }
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if(actionEvent.getSource()==New){
            input=jTextArea.getText().split("(\40|\n)+");
            count=input.length;
            array=new int[count];
            for(int i=0;i<count;i++)array[i]=Integer.parseInt(input[i]);
            start=array[0];
            Arrays.sort(array);
            JOptionPane.showMessageDialog(jFrame,"创建成功","提示",JOptionPane.INFORMATION_MESSAGE);
        }
        if(actionEvent.getSource()==SSTF){
            model.setRowCount(0);sum=0;
            int i,j,mid=0,cnt=0,left=0,right=0,tmp;
            boolean finish[]=new boolean[count];
            String rows[]=new String[2];
            for (i=0;i<count;i++){
                if(array[i]==start){
                    mid=i;finish[i]=true;break;
                }
            }
            tmp=start;
            while(cnt!=count-1){
                left=-array[count-1];right=array[0];
                for (i=mid;i<count;i++){
                    if(!finish[i]){
                        right=array[i];break;
                    }
                }
                for (j=mid;j>=0;j--){
                    if(!finish[j]){
                        left=array[j];break;
                    }
                }
                if(tmp-left>=right-tmp){
                    rows[0]=String.valueOf(right);
                    rows[1]=String.valueOf(right-tmp);
                    sum+=right-tmp;
                    tmp=right;
                    finish[i]=true;
                }
                else if(tmp-left<right-tmp){
                    rows[0]=String.valueOf(left);
                    rows[1]=String.valueOf(tmp-left);
                    sum+=tmp-left;
                    tmp=left;
                    finish[j]=true;
                }
                cnt++;
                model.addRow(rows);
            }
            JOptionPane.showMessageDialog(jFrame,String.format("平均寻道长度为%.1f",sum*1.0/(count-1)),"结果",JOptionPane.INFORMATION_MESSAGE);
        }
        if(actionEvent.getSource()==CSCAN){
            int i,op=0,tmp,next,cnt=0;
            String rows[]=new String[2];
            model.setRowCount(0);sum=0;
            for (i=0;i<count;i++){
                if(array[i]==start){
                    op=i;break;
                }
            }
            tmp=start;
            if(op!=count-1){
                next=array[op+1];op++;
            }
            else {
                next=array[0];op=0;
            }
            while(cnt!=count-1){
                rows[0]=String.valueOf(tmp);
                if(next>tmp) rows[1]=String.valueOf(next-tmp);
                else rows[1]=String.valueOf(tmp-next);
                sum+=Integer.parseInt(rows[1]);
                model.addRow(rows);
                tmp=next;
                next=array[(op+1)%count];
                op=(op+1)%count;
                cnt++;
            }
            JOptionPane.showMessageDialog(jFrame,String.format("平均寻道长度为%.1f",sum*1.0/(count-1)),"结果",JOptionPane.INFORMATION_MESSAGE);
        }
        if(actionEvent.getSource()==SCAN){
            int i,op=0,tmp;
            String rows[]=new String[2];
            model.setRowCount(0);sum=0;
            for (i=0;i<count;i++){
                if(array[i]==start){
                    op=i;break;
                }
            }
            for(i=op+1;i<count;i++){
                rows[0]=String.valueOf(array[i]);
                rows[1]=String.valueOf(array[i]-array[i-1]);
                sum+=Integer.parseInt(rows[1]);
                model.addRow(rows);
            }
            tmp=array[count-1];
            for(i=op-1;i>=0;i--){
                rows[0]=String.valueOf(array[i]);
                rows[1]=String.valueOf(tmp-array[i]);
                tmp=array[i];
                sum+=Integer.parseInt(rows[1]);
                model.addRow(rows);
            }
            JOptionPane.showMessageDialog(jFrame,String.format("平均寻道长度为%.1f",sum*1.0/(count-1)),"结果",JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
