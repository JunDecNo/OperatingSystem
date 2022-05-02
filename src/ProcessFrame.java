import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.security.Key;
import java.text.NumberFormat;
import java.util.*;

public class ProcessFrame implements ActionListener {
    private JFrame jFrame;
    private JTextArea jTextArea;
    private JButton Run,Create,Block,BtnReady,BtnBlock,BtnFinish,Empty,Wake;
    private JLabel LabelPriority,LabelTag,LabelTime;
    private JTextField Priority,Tag,Time;
    private JPanel Head,Foot;
    private JScrollPane jScrollPane;
    private String P_str,T_str,D_str;
    private int count=0;
    Comparator<Process> comparator= (process, t1) -> {
        return Integer.compare(t1.priority, process.priority);
    };
    private Queue<Process> ready=new PriorityQueue<>(comparator);
    private Queue<Process> block=new PriorityQueue<>(comparator);
    private Queue<Process> finish=new PriorityQueue<>(comparator);
    ProcessFrame(){
        jFrame=new JFrame("进程状态转换");
        Head=new JPanel();
        Foot=new JPanel();
        Priority=new JFormattedTextField(NumberFormat.getIntegerInstance());
        Time=new JFormattedTextField(NumberFormat.getIntegerInstance());
        Tag=new JTextField(20);
        LabelPriority=new JLabel("优先级");
        LabelTag=new JLabel("进程名");
        LabelTime=new JLabel("时间数");
        Run=new JButton("运行");
        Create=new JButton("创建");
        Block=new JButton("堵塞");
        Wake=new JButton("唤醒");
        BtnReady=new JButton("查看就绪队列");
        BtnBlock=new JButton("查看堵塞队列");
        BtnFinish=new JButton("查看终止队列");
        Empty=new JButton("清空终端");
        jTextArea=new JTextArea();
        jScrollPane=new JScrollPane(jTextArea);
        AddListener();
        Setting();
        jFrame.setVisible(true);
    }
    private void Setting(){
        jFrame.setBounds(100,100,1000,600);
        jTextArea.setFont(new Font("宋体",Font.PLAIN,14));
        jTextArea.setEnabled(false);
        jTextArea.setDisabledTextColor(Color.BLACK);
        Priority.setColumns(10);
        Time.setColumns(10);
    }
    private void AddListener(){
        Run.addActionListener(this);
        Create.addActionListener(this);
        Block.addActionListener(this);
        Wake.addActionListener(this);
        BtnReady.addActionListener(this);
        BtnBlock.addActionListener(this);
        BtnFinish.addActionListener(this);
        Empty.addActionListener(this);
        Create.registerKeyboardAction(this,KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0),JComponent.WHEN_IN_FOCUSED_WINDOW);
        Head.add(LabelPriority);Head.add(Priority);
        Head.add(LabelTag);Head.add(Tag);
        Head.add(LabelTime);Head.add(Time);
        Foot.add(Run);Foot.add(Create);Foot.add(Block);Foot.add(Wake);
        Foot.add(BtnReady);Foot.add(BtnBlock);Foot.add(BtnFinish);Foot.add(Empty);
        jFrame.add(Head,BorderLayout.NORTH);
        jFrame.add(jScrollPane, BorderLayout.CENTER);
        jFrame.add(Foot,BorderLayout.SOUTH);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource()==Run){
            Process process=ready.poll();
            if (process==null){
                JOptionPane.showMessageDialog(jFrame,"当前就绪队列没有程序","提示",JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            if (process.time==0)finish.offer(process);
                process.Run();
                jTextArea.append("运行完成-------------------------------\n");
                if (process.time==0)finish.offer(process);
                else ready.offer(process);
        }
        if(e.getSource()==Create){
            P_str=Priority.getText();
            T_str=Tag.getText();
            D_str=Time.getText();
            if(P_str.equals("")||T_str.equals("")||D_str.equals("")){
                JOptionPane.showMessageDialog(jFrame,"请输入完整的数据","警告",JOptionPane.ERROR_MESSAGE);
                return;
            }
            Process alone = new Process(StringToInt(P_str),StringToInt(D_str),count,T_str);
//            Process temp[]=new Process[processes.length+1];
//            System.arraycopy(processes,0,temp,0,processes.length);
//            processes=temp;
            if (ready.add(alone)){
                count++;
                Priority.setText("");
                Time.setText("");
                Tag.setText("");
                jTextArea.append("创建进程成功！\n\n");
            }else jTextArea.append("创建进程失败！\n\n");
        }
        if(e.getSource()==Block){
            jTextArea.append("将队列的对头元素堵塞\n\n");
            Process temp=ready.poll();
            block.offer(temp);
        }
        if(e.getSource()==BtnReady){
            Queue<Process> Temp=new PriorityQueue<>(ready);
            Process process;
            jTextArea.append("进程名              优先级            时间\n");
            jTextArea.append("正在进行的进程为---------------------\n");
            process=Temp.poll();
            jTextArea.append(process.OutTag+"             "+process.priority+"            "+process.time+'\n');
            jTextArea.append("所有就绪进程信息为:\n");
            while(!Temp.isEmpty()){
                process=Temp.poll();
                jTextArea.append(process.OutTag+"             "+process.priority+"            "+process.time+'\n');
            }
            jTextArea.append("\n\n");
        }
        if(e.getSource()==BtnBlock){
            Queue<Process> temp=new PriorityQueue<>(block);
            Process process;
            jTextArea.append("所有堵塞进程信息为:\n");
            jTextArea.append("进程名              优先级            时间\n");
            while(!temp.isEmpty()){
                process=temp.poll();
                jTextArea.append(process.OutTag+"             "+process.priority+"            "+process.time+'\n');
            }
            jTextArea.append("\n\n");
        }
        if (e.getSource()==BtnFinish){
            Queue<Process> T=new PriorityQueue<>(finish);
            Process process;
            jTextArea.append("所有终止进程信息为:\n");
            jTextArea.append("进程名              优先级            时间\n");
            while(!T.isEmpty()){
                process=T.poll();
                jTextArea.append(process.OutTag+"             "+process.priority+"            "+process.time+'\n');
            }
            jTextArea.append("\n\n");
        }
        if(e.getSource()==Empty){
            jTextArea.setText(null);
        }
        if(e.getSource()==Wake){
            jTextArea.append("将堵塞的对头元素就绪\n\n");
            Process temp=block.poll();
            ready.offer(temp);
        }
    }
    public static void main(String args[]){
        new ProcessFrame();
    }
    public int StringToInt(String str){
        String res="";
        for (String index:str.split(",")){
            res+=index;
        }
        return Integer.parseInt(res);
    }
}

class Process{
    public int priority;
    public int time;
    public int status=0;
    public int InTag;
    public String OutTag;
    Process(int priority,int time,int InTag,String OutTag){
        this.InTag=InTag;
        this.priority=priority;
        this.time=time;
        this.OutTag=OutTag;
    }
    public boolean Run(){
        this.time--;
        this.priority--;
        return true;
    }
}