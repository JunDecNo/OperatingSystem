import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PageReplace implements ActionListener {
    private JFrame jFrame;
    private JTextArea jTextArea;
    private JButton FIFO,LRU,New;
    private JScrollPane jScrollPane,jscrollpane;
    private JTable jTable;
    private JPanel jPanel;
    private DefaultTableModel model;
    private String input[];
    private int block,count,sum=0;
    PageReplace(){
        String column[]={};
        model=new DefaultTableModel(column,0);
        New=new JButton("创建");
        FIFO=new JButton("先进先出算法FIFO");
        LRU=new JButton("最久未使用算法LRU");
        jTable=new JTable(model);
        jFrame=new JFrame("页面置换算法");
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
        jPanel.add(New);jPanel.add(FIFO);jPanel.add(LRU);
        New.addActionListener(this);
        FIFO.addActionListener(this);
        LRU.addActionListener(this);
        jFrame.add(jScrollPane,BorderLayout.CENTER);
        jFrame.add(jPanel,BorderLayout.SOUTH);
    }
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if(actionEvent.getSource()==New){
            input=jTextArea.getText().split("(\40|\n)+");
            StringBuilder stringBuilder=new StringBuilder();
            int length=Integer.parseInt(input[0]);
            if (length>10){
                JOptionPane.showMessageDialog(jFrame,"请输入1-10的块数","错误",JOptionPane.ERROR_MESSAGE);
                return;
            }
            block=length;
            count=input.length;
            for(int i=0;i<length;i++)stringBuilder.append("块"+i+" ");
            stringBuilder.append("缺页次数 ");
            stringBuilder.append("置换过程 ");
            stringBuilder.append("当前页面");
            String result[]=String.valueOf(stringBuilder).split("\40");
            model.setColumnIdentifiers(result);
            JOptionPane.showMessageDialog(jFrame,"创建成功","提示",JOptionPane.INFORMATION_MESSAGE);
        }
        if (actionEvent.getSource()==FIFO){
            sum=0;model.setRowCount(0);
            int miss=0,i,j,k,max,maxi;
            boolean flag,isReplace;
            int arr[]=new int[block],time[]=new int[block],now;
            String tmp[]=new String[block+3];
            for (i=0;i<block;i++){
                arr[i]=-1;time[i]=0;
            }
            for (i=1;i<count;i++){
                flag=false;
                now=Integer.parseInt(input[i]);
                tmp[block+2]=String.valueOf(now);
                for(j=0;j<block;j++) {
                    if(now==arr[j]){
                        flag=true;break;
                    }
                }
                if (flag){
                  tmp[block+1]="";
                } else{
                    miss++;
                    isReplace=false;
                    for(k=0;k<block;k++){
                        if(arr[k]==-1){
                            arr[k]=now;isReplace=true;break;
                        }
                    }
                    if (isReplace){
                        tmp[block+1]="";
                    }
                    else{
                        max=0;maxi=0;
                        for(k=0;k<block;k++){
                            if (max<time[k]){
                                max=time[k];maxi=k;
                            }
                        }
                        tmp[block+1]=arr[maxi]+"<--"+now;
                        arr[maxi]=now;time[maxi]=0;
                        sum++;
                    }
                    for (k=0;k<block;k++){
                        if (arr[k]!=-1)tmp[k]=String.valueOf(arr[k]);
                        else tmp[k]="";
                    }
                    tmp[block]=String.valueOf(miss);
                }
                model.addRow(tmp);
                for (k=0;k<block;k++){
                    if(arr[k]!=-1)time[k]++;
                }
            }
            JOptionPane.showMessageDialog(jFrame,"页面置换次数为"+sum+"次","结果",JOptionPane.INFORMATION_MESSAGE);
        }
        if (actionEvent.getSource()==LRU){
            sum=0;model.setRowCount(0);
            int miss=0,i,j,k,max,maxi;
            boolean flag,isReplace;
            int arr[]=new int[block],time[]=new int[block],now;
            String tmp[]=new String[block+3];
            for (i=0;i<block;i++){
                arr[i]=-1;time[i]=0;
            }
            for (i=1;i<count;i++){
                flag=false;
                now=Integer.parseInt(input[i]);
                tmp[block+2]=String.valueOf(now);
                for(j=0;j<block;j++) {
                    if(now==arr[j]){
                        flag=true;break;
                    }
                }
                if (flag){
                    tmp[block+1]="";time[j]=0;
                } else{
                    miss++;
                    isReplace=false;
                    for(k=0;k<block;k++){
                        if(arr[k]==-1){
                            arr[k]=now;isReplace=true;break;
                        }
                    }
                    if (isReplace){
                        tmp[block+1]="";
                    }
                    else{
                        max=0;maxi=0;
                        for(k=0;k<block;k++){
                            if (max<time[k]){
                                max=time[k];maxi=k;
                            }
                        }
                        tmp[block+1]=arr[maxi]+"<--"+now;
                        arr[maxi]=now;time[maxi]=0;
                        sum++;
                    }
                    for (k=0;k<block;k++){
                        if (arr[k]!=-1)tmp[k]=String.valueOf(arr[k]);
                        else tmp[k]="";
                    }
                    tmp[block]=String.valueOf(miss);
                }
                model.addRow(tmp);
                for (k=0;k<block;k++){
                    if(arr[k]!=-1)time[k]++;
                }
            }
            JOptionPane.showMessageDialog(jFrame,"页面置换次数为"+sum+"次","结果",JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
