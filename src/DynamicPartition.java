
import javafx.css.Size;

import javax.rmi.ssl.SslRMIClientSocketFactory;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class DynamicPartition implements ActionListener {
    private JFrame jFrame;
    private JTextArea jTextArea;
    private JScrollPane jScrollPane, jscrollpane;
    private JButton Create, FF, NF, BF, WF;
    private JTable jTable;
    private JPanel jPanel;
    private DefaultTableModel model;
    private String option[],once;
    private String rows[]=new String[2];
    int length;
    DynamicPartition() {
        String column[]={"空闲块号的起始地址","对应的大小"};
        model=new DefaultTableModel(column,0);
        jFrame = new JFrame("动态分区分配算法");
        jTable=new JTable(model);
        jPanel=new JPanel();
        jTextArea=new JTextArea(10,50);
        jScrollPane=new JScrollPane(jTable);
        jscrollpane=new JScrollPane(jTextArea);
        Create = new JButton("创建");
        FF = new JButton("首次适应FirstFit");
        NF = new JButton("循环首次适应NextFit");
        BF = new JButton("最佳适应BestFit");
        WF = new JButton("最坏适应WorstFit");
        Setting();
        Add();
    }
    void Setting() {
        DefaultTableCellRenderer r = new DefaultTableCellRenderer();
        r.setHorizontalAlignment(JLabel.CENTER);
        jFrame.setLayout(new BorderLayout());
        jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        jFrame.setBounds(100, 100, 1000, 600);
        jTextArea.setDisabledTextColor(Color.BLACK);
        jTextArea.setWrapStyleWord(true);
        jTable.setDefaultRenderer(Object.class, r);
        jTable.setRowHeight(30);
        jTextArea.setFont(new Font("黑体", Font.PLAIN, 16));
        jFrame.setVisible(true);
    }
    void Add() {
        jFrame.add(jscrollpane, BorderLayout.NORTH);
        jPanel.add(Create);
        jPanel.add(FF);
        jPanel.add(NF);
        jPanel.add(BF);
        jPanel.add(WF);
        Create.addActionListener(this);
        FF.addActionListener(this);
        NF.addActionListener(this);
        BF.addActionListener(this);
        WF.addActionListener(this);
        jFrame.add(jScrollPane, BorderLayout.CENTER);
        jFrame.add(jPanel, BorderLayout.SOUTH);
    }
    public static void main(String[] args) {
        new DynamicPartition();
    }
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if(actionEvent.getSource()==Create){
            option=jTextArea.getText().split("(\40|\n)+");
            length=option.length;
//            for (int i=0;i<length;i++){
//                if(!option[i].split("([0-9])+")[0].equals("malloc")){
//
//                }
//            }
            JOptionPane.showMessageDialog(jFrame,"创建成功","提示",JOptionPane.INFORMATION_MESSAGE);
        }
        if(actionEvent.getSource()==FF){
            Partition("FF");
        }
        if(actionEvent.getSource()==NF){
            model.setRowCount(0);
            int size,id=0,ago=0;
            boolean isMalloc;
            Comparator<SizeNode> comparator= (t1, t2) -> {
                return Integer.compare(t1.addr, t2.addr);
            };
            Queue<SizeNode> AddrPriority=new PriorityQueue<>(comparator);
            Queue<SizeNode> Free=new PriorityQueue<>(comparator);
            Free.offer(new SizeNode(0,0,Integer.parseInt(option[0]),0));
            for(int i=1;i<length;i++) {
                once = option[i];isMalloc=false;SizeNode first=new SizeNode();
                size = Integer.parseInt(once.split("malloc|delete")[1]);
//                System.out.println(size);
                if (once.split("\\d")[0].equals("malloc")) {
                    Queue<SizeNode> Tmp = new PriorityQueue<>(comparator);
                    while (!Free.isEmpty()) {
                        SizeNode temp = Free.poll();
                        if(temp.size >= size&&!isMalloc&&temp.addr>=ago){
                            AddrPriority.offer(new SizeNode(id, temp.addr, size, 1));
                            temp.size -= size;
                            temp.addr += size;
                            ago=temp.addr;
                            System.out.println(ago+" ");
                            isMalloc = true;
                            id++;
                        }
                        Tmp.offer(temp);
                    }
                    Assign(Free,Tmp);
                        while (!Free.isEmpty()) {
                            SizeNode temp = Free.poll();
                            if(temp.size >= size&&!isMalloc){
                                AddrPriority.offer(new SizeNode(id, temp.addr, size, 1));
                                temp.size -= size;
                                temp.addr += size;
                                ago=temp.addr;
                                isMalloc = true;
                                id++;
                            }
                            Tmp.offer(temp);
                        }
                        Assign(Free,Tmp);
                }else{
                    Queue<SizeNode> Tmp=new PriorityQueue<>(comparator);
                    Queue<SizeNode> Tmp2=new PriorityQueue<>(comparator);
                    while (!AddrPriority.isEmpty()){
                        SizeNode temp=AddrPriority.poll();
                        if(temp.size==size){
                            Free.offer(temp);
                            int merge=Free.size();
                            SizeNode before=Free.poll(),middle=Free.poll();
                            while(!Free.isEmpty()){
                                if(before.addr+before.size==middle.addr){
                                    Free.offer(new SizeNode(before.id,before.addr,before.size+middle.size,0));
                                    before.size+=middle.size;
                                    middle=Free.poll();continue;
                                }else{
                                    Tmp2.offer(before);
                                }
                                before=middle;
                                middle=Free.poll();
                            }
                            Tmp2.offer(before);Tmp2.offer(middle);
                            Assign(Free,Tmp2);
//                            System.out.println("释放了"+size);
                        }else Tmp.offer(temp);
                    }
                    Assign(AddrPriority,Tmp);
                }
            }
            while(!Free.isEmpty()){
                SizeNode temp=Free.poll();
                rows[0]=String.valueOf(temp.addr);
                rows[1]=String.valueOf(temp.size);
                model.addRow(rows);
            }
        }
        if(actionEvent.getSource()==BF){
            Partition("BF");
        }
        if(actionEvent.getSource()==WF){
            Partition("WF");
        }
    }
    public void Partition(String string){
        model.setRowCount(0);
        Comparator<SizeNode> comparator= (t1, t2) -> {
            return Integer.compare(t1.addr, t2.addr);
        };
        if(string.equals("FF")){
            comparator= (t1, t2) -> {
                return Integer.compare(t1.addr, t2.addr);
            };
        }else if(string.equals("NF")){

        }else if(string.equals("BF")){
            comparator= (t1, t2) -> {
                return Integer.compare(t1.size, t2.size);
            };
        }else if(string.equals("WF")){
            comparator= (t2, t1) -> {
                return Integer.compare(t1.size, t2.size);
            };
        }
            int size,id=0;
            boolean isMalloc=false;
            Queue<SizeNode> AddrPriority=new PriorityQueue<>(comparator);
            Queue<SizeNode> Free=new PriorityQueue<>(comparator);
            Free.offer(new SizeNode(0,0,Integer.parseInt(option[0]),0));
            for(int i=1;i<length;i++) {
                once = option[i];isMalloc=false;
                size = Integer.parseInt(once.split("malloc|delete")[1]);
                if (once.split("\\d")[0].equals("malloc")) {
                    Queue<SizeNode> Tmp = new PriorityQueue<>(comparator);
                    while (!Free.isEmpty()) {
                        SizeNode temp = Free.poll();
                        if (temp.size >= size&&!isMalloc) {
                            AddrPriority.offer(new SizeNode(id, temp.addr, size, 1));
                            temp.size -= size;
                            temp.addr += size;
                            isMalloc = true;
                            id++;
                        }
                        Tmp.offer(temp);
                    }
                    Assign(Free,Tmp);
                }else{
                    Queue<SizeNode> Tmp=new PriorityQueue<>(comparator);
                    Queue<SizeNode> Tmp2=new PriorityQueue<>(comparator);
                    while (!AddrPriority.isEmpty()){
                        SizeNode temp=AddrPriority.poll();
                        if(temp.size==size){
                            Free.offer(temp);
                            int merge=Free.size();
                            SizeNode before=Free.poll(),middle=Free.poll();
                            while(!Free.isEmpty()){
                                if(before.addr+before.size==middle.addr){
                                    Free.offer(new SizeNode(before.id,before.addr,before.size+middle.size,0));
                                    before.size+=middle.size;
                                    middle=Free.poll();continue;
                                }else{
                                    Tmp2.offer(before);
                                }
                                before=middle;
                                middle=Free.poll();
                            }
                            Tmp2.offer(before);Tmp2.offer(middle);
                            Assign(Free,Tmp2);
                        }else Tmp.offer(temp);
                    }
                    Assign(AddrPriority,Tmp);
                }
            }
            while(!Free.isEmpty()){
                SizeNode temp=Free.poll();
                rows[0]=String.valueOf(temp.addr);
                rows[1]=String.valueOf(temp.size);
                model.addRow(rows);
            }
    }
    public void Assign(Queue<SizeNode> destination,Queue<SizeNode> source){
        while(!source.isEmpty()){
            destination.offer(source.poll());
        }
    }
}
class SizeNode{
    public int id,addr,size,status,before;
    SizeNode(){}
    SizeNode(SizeNode source){
        this.id=source.id;
        this.addr=source.addr;
        this.size=source.size;
        this.status=source.status;
    }
    SizeNode(int id,int addr,int size,int status){
        this.id=id;
        this.addr=addr;
        this.size=size;
        this.status=status;
    }
}