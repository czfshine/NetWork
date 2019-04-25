package file;

import cn.czfshine.network.p2p.client.file.DirInfo;
import cn.czfshine.network.p2p.client.file.Downloader;
import cn.czsgine.network.p2p.common.dto.ClientInfo;
import cn.czsgine.network.p2p.common.dto.ClientList;
import cn.czsgine.network.p2p.common.dto.FileInfo;
import cn.czsgine.network.p2p.common.dto.LoginMessage;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After; 

/** 
* Downloader Tester. 
* 
* @author <Authors name>
* @version 1.0 
*/ 
public class DownloaderTest { 

@Before
public void before() throws Exception { 
} 

@After
public void after() throws Exception { 
} 

/** 
* 
* Method: setRecvBlock(RecvBlock recvBlock) 
* 
*/ 
@Test
public void testSetRecvBlock() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: setLogger(Logger logger) 
* 
*/ 
@Test
public void testSetLogger() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: start() 
* 
*/ 
@Test
public void testStart() throws Exception { 
//TODO: Test goes here...
    DirInfo.dirname="./B/";
    ClientList clientList = new ClientList();
    ClientInfo clientInfo = new ClientInfo();
    clientInfo.setLogininfo(new LoginMessage("127.0.0.1",2333,2333));
    clientList.add(clientInfo);
    clientInfo = new ClientInfo();
    clientInfo.setLogininfo(new LoginMessage("127.0.0.1",2334,2333));
    clientList.add(clientInfo);
    clientInfo = new ClientInfo();
    clientInfo.setLogininfo(new LoginMessage("127.0.0.1",2335,2333));
    clientList.add(clientInfo);
    FileInfo fileInfo = new FileInfo();
    fileInfo.setFilename("1.txt");
    fileInfo.setSize(12586752L);
    clientList.setFileInfo(fileInfo);
    new Downloader("1.txt",clientList).start();
    System.in.read();
} 


/** 
* 
* Method: defrecv(FileClient fileClient, BlockResponse blockResponse) 
* 
*/ 
@Test
public void testDefrecv() throws Exception { 
//TODO: Test goes here... 
/* 
try { 
   Method method = Downloader.getClass().getMethod("defrecv", FileClient.class, BlockResponse.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/ 
} 

/** 
* 
* Method: deflog(String msg) 
* 
*/ 
@Test
public void testDeflog() throws Exception { 
//TODO: Test goes here... 
/* 
try { 
   Method method = Downloader.getClass().getMethod("deflog", String.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/ 
} 

/** 
* 
* Method: downloadBlock(int seq) 
* 
*/ 
@Test
public void testDownloadBlock() throws Exception { 
//TODO: Test goes here... 
/* 
try { 
   Method method = Downloader.getClass().getMethod("downloadBlock", int.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/ 
} 

/** 
* 
* Method: recvBlock(FileClient fileClient, BlockResponse blockResponse) 
* 
*/ 
@Test
public void testRecvBlock() throws Exception { 
//TODO: Test goes here... 
/* 
try { 
   Method method = Downloader.getClass().getMethod("recvBlock", FileClient.class, BlockResponse.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/ 
} 

/** 
* 
* Method: writeToOutputFile(BlockResponse blockResponse) 
* 
*/ 
@Test
public void testWriteToOutputFile() throws Exception { 
//TODO: Test goes here... 
/* 
try { 
   Method method = Downloader.getClass().getMethod("writeToOutputFile", BlockResponse.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/ 
} 

} 
