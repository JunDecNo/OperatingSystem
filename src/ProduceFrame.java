import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class ProduceFrame implements ActionListener {
    private JFrame jFrame;
    private JPanel North,South;
    private JTextArea jTextArea;
    private JScrollPane jScrollPane;
    private JLabel jLabel;
    private JTextField jTextField;
    private JButton BtnPro,BtnCon,BtnEmpty,BtnFind,BtnTest,BtnStop;
    private int Length=100;
    private int mutex=1,empty=Length,full=0;
    private Queue<String> queue=new LinkedList<>();
    private Queue<Thread> ThreadQueue=new LinkedList<>();
    ProduceFrame(){//定义一些组件和容器
        jFrame=new JFrame("生产者-消费者问题");
        jTextArea=new JTextArea();
        jScrollPane=new JScrollPane(jTextArea);
        North=new JPanel();
        South=new JPanel();
        BtnPro=new JButton("生产");
        BtnCon=new JButton("消费");
        BtnFind=new JButton("查看产品栏");
        BtnEmpty=new JButton("清空终端");
        BtnTest=new JButton("测试生产者-消费者问题");
        BtnStop=new JButton("停止");
        jLabel=new JLabel("生产物品名");
        jTextField=new JTextField(20);
        Add();Setting();
    }
    private void Add(){
        BtnPro.addActionListener(this);
        BtnCon.addActionListener(this);
        BtnEmpty.addActionListener(this);
        BtnFind.addActionListener(this);
        BtnTest.addActionListener(this);
        BtnStop.addActionListener(this);
        North.add(jLabel);North.add(jTextField);
        South.add(BtnPro);South.add(BtnCon);South.add(BtnEmpty);
        South.add(BtnFind);South.add(BtnTest);South.add(BtnStop);
        jFrame.add(jScrollPane, BorderLayout.CENTER);
        jFrame.add(North,BorderLayout.NORTH);
        jFrame.add(South,BorderLayout.SOUTH);
    }
    private void Setting(){
        jFrame.setBounds(100,100,1000,600);
        jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        jTextArea.setDisabledTextColor(Color.black);
        jTextArea.setFont(new Font("宋体",Font.PLAIN,14));
        jTextArea.setEditable(false);
        jFrame.setVisible(true);
    }
    private void producer(String string){//生产
            wait("empty");
            wait("mutex");
            queue.offer(string);
            jTextArea.append("生产了"+string+'\n');
            signal("mutex");
            signal("full");
    }
    private void consumer(){//消费
            wait("full");
            wait("mutex");
            String string=queue.poll();
            jTextArea.append("消费了"+string+'\n');
            signal("mutex");
            signal("empty");
    }
    //分别是wait和signal方法
    private void wait(String string ){
        int i=0;
        if (string.equals("empty")){
            while(this.empty<=0)i++;
            this.empty--;
        }
        if(string.equals("full")){
            while(this.full<=0)i++;
            this.full--;
        }
        if (string.equals("mutex")){
            while(this.mutex<=0)i++;
            this.mutex--;
        }
    }
    private void signal(String string){
        if (string.equals("empty")){
            this.empty++;
        }
        if(string.equals("full")){
            this.full++;
        }
        if (string.equals("mutex")){
            this.mutex++;
        }
    }
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource()==BtnStop){
            new ProduceFrame();
            jFrame.dispose();//用于停止,直接关闭窗口,产生新的窗口
        }
        if (actionEvent.getSource()==BtnCon){
            if (queue.isEmpty()){
                jTextArea.append("无产品可消费\n");
                return;
            }
            this.consumer();//消费
        }
        if(actionEvent.getSource()==BtnPro){
            String string=jTextField.getText();
            if(jTextField.getText().equals("")){
                JOptionPane.showMessageDialog(jFrame,"请输入产品号","提示",JOptionPane.ERROR_MESSAGE);//输入
                jTextArea.append("生产失败\n\n");
                return;
            }
            this.producer(string);//生产
            jTextField.setText("");
            jTextArea.append("生产成功\n\n");
        }
        if(actionEvent.getSource()==BtnEmpty)jTextArea.setText("");
        if(actionEvent.getSource()==BtnFind){
            Queue<String> temp=new LinkedList<>(queue);//查看对垒中元素
            jTextArea.append("剩余产品为：\n");
            while (!temp.isEmpty()){
                jTextArea.append(temp.poll()+"\n");
            }
        }
        if(actionEvent.getSource()==BtnTest){
            String string=JOptionPane.showInputDialog(jFrame,"输入进程数量和各个的延迟时间(ms)","输入进程信息",JOptionPane.INFORMATION_MESSAGE);
            String array[]=string.split("\40");
            int len=Integer.parseInt(array[0]);
            if(len!=array.length-1){//如果输入长度不匹配则说明输入有误
                JOptionPane.showMessageDialog(jFrame,"输入非法","错误",JOptionPane.ERROR_MESSAGE);
                return;
            }
            Storage storage = new Storage(this.jTextArea);
            for(int i=1;i<len;i+=2){//建立与输入相匹配的线程
                Thread thread1=new Thread(new Producer(storage,Integer.parseInt(array[i])));
                Thread thread2=new Thread(new Consumer(storage,Integer.parseInt(array[i+1])));
                ThreadQueue.offer(thread1);ThreadQueue.offer(thread2);
                thread1.start();
                thread2.start();
            }
        }
    }
    //用于生成随机字符串
    public static String getRandomString(int strLength, int strType) {
        String ALLCHAR;
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        if (strType == 2) {
            ALLCHAR = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        } else {
            ALLCHAR = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        }
        for (int i = 0; i < Math.abs(strLength); i++) {
            sb.append(ALLCHAR.charAt(random.nextInt(ALLCHAR.length())));
        }
        return sb.toString();
    }
}
class Consumer implements Runnable{
    private Storage storage;
    private int time;
    public Consumer(Storage storage,int time){//初始化
        this.storage = storage;
        this.time=time;
    }
    @Override
    public void run(){
        while(true){
            try{
                Thread.sleep(time);//睡眠,便于观察
                storage.consume();//消费
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }
}
class Producer implements Runnable{
    private Storage storage;
    private int time;
    public Producer(Storage storage,int time){//初始化数据
        this.storage = storage;
        this.time=time;
    }
    @Override
    public void run(){
        while(true){
            try{
                Thread.sleep(time);
                storage.produce();//生产
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }
}
class Storage {
    // 仓库存储的载体
    private LinkedList<String> list = new LinkedList<String>();
    private JTextArea jTextArea;
    // 仓库的最大容量
    final Semaphore notFull = new Semaphore(10);
    // 将线程挂起，等待其他来触发
    final Semaphore notEmpty = new Semaphore(0);
    // 互斥锁
    final Semaphore smutex = new Semaphore(1);
    Storage(JTextArea jTextArea){
        this.jTextArea=jTextArea;
    }
    public void produce() {
        try {
            notFull.acquire();//顺序不能颠倒，否则会造成死锁。
            smutex.acquire();
            String string= ProduceFrame.getRandomString(5,1);
            list.offer(string);
            jTextArea.append("生产者生产了["+string+"]  现存"+list.size()+"\n");
        }
        catch (Exception e) {
            e.printStackTrace();
        } finally {
            smutex.release();
            notEmpty.release();
        }
    }

    public void consume()
    {
        try {
            notEmpty.acquire();//顺序不能颠倒，否则会造成死锁。
            smutex.acquire();
            String string=list.poll();
            jTextArea.append("消费者消费了["+string+"]  现存"+list.size()+"\n");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            smutex.release();
            notFull.release();
        }
    }
}

