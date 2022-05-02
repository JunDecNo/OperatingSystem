import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

public class ProcessScheduling implements ActionListener {
    private JFrame jFrame;
    private JPanel jPanel;
    private JButton Init,RRS,MFQ,HRR,Switch,Hand;
    private JTextArea jTextArea;
    private JScrollPane jScrollPane,jscrollpane;
    private FileDialog open;
    private int Time=0,flag=1,relog=1;
    private JTable jTable;
    private String now;
    Comparator<PCB> comparator= (t2, t1) -> {
        return Double.compare(t1.response, t2.response);
    };
    Comparator<PCB> comparator1= (t2, t1) -> {
        return t2.name.compareTo(t1.name);
    };
    private Queue<PCB>InIt=new LinkedList<>(),Ready=new LinkedList<>(),Finish=new PriorityQueue<>(comparator1);
    private Queue<PCB>One=new LinkedList<>(),Two=new LinkedList<>(),Three=new LinkedList<>();
    private Queue<PCB>RRSReady=new LinkedList<>();
    private Queue<PCB>Priority=new PriorityQueue<PCB>(comparator);
    private DefaultTableModel model,result;
    ProcessScheduling(){
        String column[]={"进程名","开始时间","服务时间", "完成时间",   "周转时间" ,  "带权周转时间"};
        String reslumn[]={"进程名","服务时间","剩余时间","等待时间","响应比"};
        model=new DefaultTableModel(column,0);
        result=new DefaultTableModel(reslumn,0);
        jTable=new JTable(model);
        jFrame=new JFrame("进程调度");
        open=new FileDialog(jFrame,"打开文件",FileDialog.LOAD);
        jPanel=new JPanel();
        Hand=new JButton("手动创建");
        Init=new JButton("初始化");
        RRS=new JButton("时间片轮转调度");
        MFQ=new JButton("多级队列反馈调度");
        HRR=new JButton("高响应比优先调度");
        Switch=new JButton("结果队列/过程队列 Switch");
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
        jTable.setDefaultRenderer(Object.class,r);
        jTable.setRowHeight(30);
        jTextArea.setFont(new Font("黑体",Font.PLAIN,16));
        jFrame.setVisible(true);
    }
    void Add(){
        jFrame.add(jscrollpane,BorderLayout.NORTH);
        jPanel.add(Init);jPanel.add(Hand);jPanel.add(RRS);jPanel.add(MFQ);jPanel.add(HRR);jPanel.add(Switch);
        Hand.addActionListener(this);
        Init.addActionListener(this);
        RRS.addActionListener(this);
        MFQ.addActionListener(this);
        HRR.addActionListener(this);
        Switch.addActionListener(this);
        jFrame.add(jScrollPane,BorderLayout.CENTER);
        jFrame.add(jPanel,BorderLayout.SOUTH);
    }
    void RRS_Run(boolean b){
        while(!Ready.isEmpty()&&Ready.element().start<=Time)RRSReady.offer(Ready.poll());
        if (!RRSReady.isEmpty()){
            PCB res=RRSReady.poll();
            res.run(1,result);Time++;
            if(b)jTextArea.append(res.name+" ");
            if (res.time==0){
                res.end=Time;
                Finish.offer(res);
            }else{
                while(!Ready.isEmpty()&&Ready.element().start<=Time)RRSReady.offer(Ready.poll());
                RRSReady.offer(res);
            }
        }

    }
    void MFQ_Run(boolean b){
        while(!Ready.isEmpty()&&Ready.element().start<=Time)One.offer(Ready.poll());
        if(!One.isEmpty()){
            PCB res=One.poll();
            res.run(1,result);Time++;
            if(b)jTextArea.append(res.name+" ");
            if (res.time<=0){
                res.end=Time;
                Finish.offer(res);
            }else{
                while(!Ready.isEmpty()&&Ready.element().start<=Time)One.offer(Ready.poll());
                Two.offer(res);
            }
        }
        else if(!Two.isEmpty()){
            PCB res=Two.poll();
            res.run(2,result);Time+=2;
            if(b)jTextArea.append(res.name+" ");
            if (res.time<=0){
                res.server+=res.time;
                Time+=res.time;
                res.end=Time;
                res.time=0;
                Finish.offer(res);
            }else{
                while(!Ready.isEmpty()&&Ready.element().start<=Time)One.offer(Ready.poll());
                Three.offer(res);
            }
        }
        else if(!Three.isEmpty()){
            PCB res=Three.poll();
            res.run(4,result);Time+=4;
            if(b)jTextArea.append(res.name+" ");
            if (res.time<=0){
                res.server+=res.time;
                Time+=res.time;
                res.end=Time;
                res.time=0;
                Finish.offer(res);
            }else{
                while(!Ready.isEmpty()&&Ready.element().start<=Time)One.offer(Ready.poll());
                Three.offer(res);
            }
        }
    }
    void HRR_Run(boolean b){
        while(!Ready.isEmpty()&&Ready.element().start<=Time)Priority.offer(Ready.poll());
        if (!Priority.isEmpty()){
            PCB res=Priority.poll();
            res.run(1,result);Time++;
            if(b)jTextArea.append(res.name+" ");
            int len=Priority.size();
            for(int i=0;i<len;i++){
                PCB t=Priority.poll();
                t.Wait();
                Priority.offer(t);
            }
            if (res.time<=0){
                res.end=Time;
                Finish.offer(res);
            }else{
                while(!Ready.isEmpty()&&Ready.element().start<=Time)Priority.offer(Ready.poll());
                Priority.offer(res);
            }
        }

    }
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource()==Init){
            InIt.clear();
            File file;
            open.setVisible(true);
            String DirPath=open.getDirectory();
            String FileName=open.getFile();
            if (DirPath!=null&&FileName!=null){
                file=new File(DirPath,FileName);
                jTextArea.setText(null);
                try {
                    BufferedReader bufferedReader=new BufferedReader(new FileReader(file));
                    String line=null;
                    while((line=bufferedReader.readLine())!=null){
                        String []s=line.split("\40");
                        InIt.offer(new PCB(s[0],Integer.parseInt(s[1]),Integer.parseInt(s[2]),Integer.parseInt(s[3]),Integer.parseInt(s[4]),Integer.parseInt(s[5]),Integer.parseInt(s[6])));
                    }
                    bufferedReader.close();
                    jTextArea.append("初始化成功\n");
                    flag=0;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if(actionEvent.getSource()==RRS){
            Copy();
            if (flag==1){
                JOptionPane.showMessageDialog(jFrame, "未添加数据","提示",JOptionPane.ERROR_MESSAGE);
                return;
            }
            while (!Ready.isEmpty()||!RRSReady.isEmpty())RRS_Run(true);
            this.sum(Finish,"时间片轮转调度算法",true);
            relog=1;
        }
        if (actionEvent.getSource()==MFQ){
            Copy();
            if (flag==1){
                JOptionPane.showMessageDialog(jFrame, "未添加数据","提示",JOptionPane.ERROR_MESSAGE);
                return;
            }
            while(!Ready.isEmpty()||!Three.isEmpty()||!Two.isEmpty()||!One.isEmpty())MFQ_Run(true);
            this.sum(Finish,"多级反馈队列调度算法",true);
            relog=1;
        }
        if(actionEvent.getSource()==HRR){
            Copy();
            if (flag==1){
                JOptionPane.showMessageDialog(jFrame, "未添加数据","提示",JOptionPane.ERROR_MESSAGE);
                return;
            }
//            System.out.println(Priority.poll().response+" "+Priority.poll().response);
            while(!Ready.isEmpty()||!Priority.isEmpty())HRR_Run(true);
            this.sum(Finish,"高响应比优先调度算法",true);
            relog=1;
        }
        if(actionEvent.getSource()==Switch){
            if (relog==1){
                model.setRowCount(0);
                String reslumn[]={"进程名","服务时间","剩余时间","等待时间","响应比"};
                model.setColumnIdentifiers(reslumn);
                int len=result.getRowCount();
                for (int i=0;i<len;i++){
                    String s[]= {result.getValueAt(i, 0).toString(),
                            result.getValueAt(i, 1).toString(),
                            result.getValueAt(i, 2).toString(),
                            result.getValueAt(i, 3).toString(),
                            result.getValueAt(i, 4).toString(),
                    };
                    model.addRow(s);
                }
            }else{
                Copy();
                if(now.equals("高响应比优先调度算法")){
                    while(!Ready.isEmpty()||!Priority.isEmpty())HRR_Run(false);
                    this.sum(Finish,"高响应比优先调度算法",false);
                }
                else if (now.equals("时间片轮转调度算法")){
                    while (!Ready.isEmpty()||!RRSReady.isEmpty())RRS_Run(false);
                    this.sum(Finish,"时间片轮转调度算法",false);
                }
                else if(now.equals("多级反馈队列调度算法")){
                    while(!Ready.isEmpty()||!Three.isEmpty()||!Two.isEmpty()||!One.isEmpty())MFQ_Run(false);
                    this.sum(Finish,"多级反馈队列调度算法",false);
                }
            }
            relog^=1;
        }
    }
    void Copy(){
        Finish.clear();Ready.clear();result.setRowCount(0);
        RRSReady.clear();One.clear();Two.clear();Three.clear();
        model.setRowCount(0);
        int len=InIt.size();
        for(int i=0;i<len;i++){
            PCB pcb=new PCB(InIt.element());
            Ready.offer(pcb);
            InIt.offer(InIt.poll());
        }
    }
    void sum(Queue<PCB> queue,String string,boolean b){
        int cycle,finish,length=queue.size();
        double wight,average=0;
//        jTextArea.append("\n进程名   开始时间   服务时间   完成时间   周转时间   带权周转时间\n");
        int len=queue.size();
        for(int i=0;i<len;i++){
            PCB t=queue.poll();
            cycle=t.end-t.start;
            finish=t.end;
            wight=1.0*cycle/t.server;
            average+=wight;
            String []str=String.format("%s %d %d %d %d %.3f\n", t.name, t.start, t.server, finish, cycle, wight).split("\40");
            model.addRow(str);
        }
        if(b)jTextArea.append(string+"的平均带权周转时间为："+String.format("%.3f",average/length)+"\n\n");
        this.Time=0;this.now=string;
    }
    public static void main(String []args){
        new ProcessScheduling();
    }
}
class PCB{
    public int id,start,end,server,wait,time;
    public double response=1;
    public String name;
    PCB(String name,int id,int start,int time,int server,int wait,int end){
        this.name=name;
        this.id=id;
        this.server=server;
        this.start=start;
        this.wait=wait;
        this.end=end;
        this.time=time;
    }
    PCB(PCB source){
        this.name=source.name;
        this.id=source.id;
        this.server=source.server;
        this.start=source.start;
        this.wait=source.wait;
        this.end=source.end;
        this.time=source.time;
        this.response=source.response;
    }
    void run(int time,DefaultTableModel model){
        int tt,ts;
        this.time-=time;
        this.server+=time;
        tt=this.time>0?this.time:0;
        ts=this.time>0?this.server:this.server+this.time;
        String string[]=String.format("%s %d %d %d %.3f\n",name,ts,tt,wait,response).split("\40");
        this.response=1;this.wait=0;
        model.addRow(string);
    }
    void Wait(){
        wait++;
        response=1+1.0*wait/time;
    }
}
