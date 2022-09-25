package javaapplication2;

import CYICE.*;
import java.util.*;
import java.lang.*;

class G {

  static int sTestNum = 0 ;
  static public ICEInputStream sIn ;
  
  static public void Init() throws Throwable {
    sIn = new ICEInputStream() ;      
  } // Init()
  
  static void CYPrint( String str ) throws Throwable {
     byte[] binary = str.getBytes( "Big5" ) ;
     System.out.write( binary, 0, binary.length ) ;
     
  } // CYPrint()
        
} // class G

class Editor {
  static int snum = -1; 
  static Vector<Buffer> sbuffer = new Vector<Buffer>() ; 

  static void 初始化()  {
      
    Buffer buffer = new Buffer();
    sbuffer.add( buffer ) ;
    snum++ ; 
    
  }  // 初始化() 
  
  static void 處理 ( String word ) throws Throwable {
        
    if ( word.charAt( 0 ) == 'l' ) {
      順序列出() ;    // 把所有的buffer的名字依照buffer產生的順序列出來
    } // if
    else if ( word.charAt( 0 ) == 'n' )  { // 看前面是否為N
      產生() ;
    } // else if 
    else if ( word.charAt( 0 ) == 'c' )  { // 看前面是否為C
      切換() ; 
    } // else if 
    else if ( word.charAt( 0 ) == 'b') { // 看前面是否為B
      印出() ;
    } // else if 
    
  } // 處理()

  static void 順序列出() throws Throwable { // --------ld------
      
    for ( int i = 0 ; i < sbuffer.size() ; i++ ) { 
          if ( sbuffer.get( i ).mbuffername != "" ) {
              G. CYPrint( sbuffer.get( i ).mbuffername + " "); 
              G. CYPrint( sbuffer.get( i ).mline.size() + "\n");
          } // if
          else {
              G.CYPrint( "Buffer-" + i + " " + sbuffer.get( i ).mline.size() + "\n"); 
          } // else
      
    } // for
  } // 順序列出()

  static void 產生() throws Throwable { // --------nd-----
      
    初始化(); 
    String str = ""; // 名字的字串
    char ch = '\0' ; // nb name之間有空白，或是換行
    ch = G.sIn.ReadChar() ;
    if ( ch == ' ' ) { // 有名字要更新
          str = G.sIn.ReadString() ; // 讀名字
          snum = sbuffer.size() - 1 ;
          sbuffer.get( snum ).mbuffername = str ;
    } // if 
    else {
          sbuffer.get( snum ).mbuffername = "Buffer-" + snum ;
    } // else 
   
  } //  產生()
 

static void  切換() throws Throwable { // ------------cd------------
  String str = "" ; // 名字的字串
  str = G.sIn.ReadString() ; // 讀名字
  int changenum = 0 ; // 看第幾個 
  boolean havesame = false ;

  for ( int i = 0 ; i < sbuffer.size() && !havesame ; i++  ) {

    if (  sbuffer.get(i).mbuffername.equals( str ) ) havesame = true ;
    changenum++ ;

  }  // for

    if ( !havesame ) {
      System.out.println( "Error : There is no such buffer! Please try again."  ) ;
    } // if
    else {
      snum = changenum - 1 ; // 為第幾個，但vector從-1開始
    } // else 

  } // 切換()

static void 印出()  throws Throwable { // --------------b--------------
      char ch = '\0' ; // b name之間有空白，或是換行
      ch = G.sIn.ReadChar() ;
      String str = "" ; // 名字的字串
      
      if ( ch == ' '  ) { // 有名字要更新
            
            str = G.sIn.ReadString() ; // 讀名字
            sbuffer.get( snum ).mbuffername = str ;
      } // if
      else if (  sbuffer.get( snum ).mbuffername.compareTo( "" ) == 0 ) {
        G.CYPrint( "Buffer-" + snum + " " + sbuffer.get( snum ).mline.size() + "\n" ) ; // Buffer-第幾個 幾行
      } // else if 
      else {
            
            G.CYPrint( sbuffer.get( snum ).mbuffername + " "  + sbuffer.get(snum).mline.size() + "\n" ) ;
            
      } // else
      
  } // 印出()

} // class Editor

class Buffer { 
  
  Vector<String> mline = new Vector<String>() ;
  int mlinenum = 0 ; //接下來第幾行
  String mbuffername = "" ; // buffer的名
  
  void ADD() throws Throwable {
        
      String str = new String() ;
      str = G.sIn.ReadInputLine() ; // 要加入的句子
      
      while ( str.compareTo( "." ) != 0 ) {
            mline.add( mlinenum, str ) ;
            mlinenum++ ;
            str = G.sIn.ReadInputLine() ; 
      }  // while

  } // ADD()
  
  void INSERT() throws Throwable {
        String str = new String() ;
        if ( mline.isEmpty() ) {
              ADD() ;  // 如果一開始就是i，如同ADD()
        } // if
        else {
              str = G.sIn.ReadInputLine() ;
              while ( str.compareTo( "." ) != 0 ) {
                    mline.insertElementAt( str, mlinenum-1 ); // 要插入的句子
                    mlinenum++ ;
                    str = G.sIn.ReadInputLine() ; // 要插入的句子
              } // while
        } // else
        
  } // INSERT()
  
  void CHANGE() throws Throwable {
        
        if ( mline.isEmpty() ) {
              System.out.println( "Error : There is no data! Please try again." ) ;
        } // if
        else {
              String last = new String() ;
              last = mline.lastElement() ;
              if  ( last.compareTo( mline.get( mlinenum-1 ) ) == 0 ) {
                    mline.remove( mlinenum -1 ) ;
                    mlinenum-- ;
                    ADD() ;
              } // if
              else {
                    String str = new String() ;
                    str = G.sIn.ReadInputLine() ;
                    mline.remove( mlinenum - 1 ) ; // 移除掉一個而已，不用c一行remove一次
                    while ( str.compareTo( "." ) != 0  ) {
                          
                          mline.insertElementAt( str, mlinenum - 1 );
                          mlinenum++ ;
                          str = G.sIn.ReadInputLine() ;
                          
                    } // while
                    
                    mlinenum -- ;
              } // else
              
        } // else
        
  } // CHANGE()
  
  void DELETE() throws Throwable {
        
        if ( mline.isEmpty() ) {
              System.out.println( "Error : There is no data! Please try again." ) ;
        } // if
        else {
             String last = new String() ;
              last = mline.lastElement() ;
              if ( last.compareTo( mline.get( mlinenum -1 ) ) == 0 ) {
                    mline.remove( mlinenum -1 ) ;
                    mlinenum-- ;
              } // if
              else {
                    mline.remove( mlinenum -1 ) ;
              } // else
              
        } // else
        
  } // DELETE()
  
  void PRINTALL() throws Throwable {
        
        for ( int i = 0 ; i < mline.size() ; i++ ) {
              G.CYPrint( mline.get( i ) + "\n" ) ;
        } // for
        
  } // PRINTALL()
  
  void 數字( String word ) throws Throwable {
        
        int num = Main.DigitInString( word ) ; // 把字串裡的數字取出來
        char ch = Main.Skipspace() ; // 回傳的為指令，多餘的部分以讀掉，若無指令return的是'\0'
        if ( num > mline.size() ) { // -----給的數字超出範圍--------
              System.out.println( "Error : Line number out of range! Please try again." ) ;
        } // if 
        else {                                 // -----給的數字正常範圍-------
              int num2 = 0 ; // 站存currentline，function從linenum來用，先改mlinenum為給的數字，再改回來
              num2 = mlinenum ; // 暫存
              mlinenum = num ; // 改為他給的數字，跑function才能用
              
              if ( word.contains( "a" ) || ch == 'a' ) { // 有數字且a指令，印出來且設定linenum為數字
                // 有可能指令在word裡面 或 ch 裡
                  ADD() ; // 加入----------------
              } // if
              else if ( word.contains( "i" ) || ch == 'i' ) {  // 有數字且i指令，印出來且設定linenum為數字
                                 // 有可能指令在word裡面 或 ch 裡
                  INSERT() ;
              } // else if 
              else if ( word.contains( "c" ) || ch == 'c' ) {  // 有數字且c指令，印出來且設定linenum為數字
                               // 有可能指令在word裡面 或 ch 裡
                  CHANGE() ;
              } // else if 
              else if ( word.contains( "d" ) || ch == 'd' ) {  // 有數字且d指令
                                // 有可能指令在word裡面 或 ch 裡
                  if ( num == mline.size() && mline.size() == num2 ) num2 -- ;
                  DELETE() ;
                  mlinenum = num2 ;
                  
              } // else if 
              else if ( word.contains( "p" ) || ch == 'p'  ) { // 有數字且p，印出來無設定
                    // 有可能指令在word裡面 或ch 裡
                    G.CYPrint( mline.get( mlinenum -1 ) + "\n" ) ; // -1 是因為存的值
                    mlinenum = num2 ;
                    
              } // else if 
              else { // 只有數字而已，印出來且設定linenum ; 先前已設定
                    
                    System.out.print( mlinenum + " : " ) ;
                    G.CYPrint( mline.get( mlinenum - 1 ) + "\n" ) ; // -1 是因為存的值
               
              } // else
              
        } // else
              
    } // 數字()
      
  void  遇到$ ( String word ) throws Throwable {
        
           char ch = Main.Skipspace() ; // 回傳的為指令，多餘的部分以讀掉
            int lastline = mline.size() ; // 設定為最後一行
            int num2 = mlinenum ; // 跟上面的數字function一樣，暫存現在的mlinenum
            mlinenum = lastline ; // 設定為最後一行
            
            if ( word.contains( "a" ) || ch == 'a' ) { // 有數字且a指令，印出來且設定為最後一行
                  ADD()  ; // 加入-------------
            } // if 
            else if ( word.contains( "i" ) || ch == 'i' ) { // 有數字且i指令，印出來且設定為最後一行
                  INSERT()  ;
            } // else if 
            else if ( word.contains( "c" ) || ch == 'c' ) { // 有數字且c指令，印出來且設定為最後一行
                  CHANGE()  ; 
            } // else if 
            else if ( word.contains( "d" ) || ch == 'd' ) { // 有數字且d指令
                  DELETE()  ;
                  mlinenum = num2  ;
            } // else if 
            else if ( word.contains( "p" ) || ch == 'p' ) { // 有數字且p指令，印出來無設定
                  G.CYPrint( mline.get( mlinenum -1  ) + "\n" )  ; // -1 因為存的值是接下來
                  mlinenum = num2  ;
            } // else if 
            else  { // 只有$$$$，印出來且設定為最後一行
                  
                  System.out.print( mlinenum + " : " )  ;
                  G.CYPrint( mline.get( mlinenum - 1 ) + "\n" )  ; // -1 因為存的值是接下來
            } // else
            
         
    } // 遇到$()
    
    
} // class Buffer
  
class Main {

  static char Skipspace() throws Throwable {
        char ch  = '\0', order = '\0' ;
        ch = G.sIn.ReadChar() ;
        boolean gotit = false ;
        while ( ch != '\n' ) { // 讀到換行，把傳回去的字元後面讀完了/清掉了，傳回去的值為第一個字元
              if ( ch != ' ' || ch != '\n' && gotit == false  ) { // gotit是怕一直更改order
                    order = ch ;
                    gotit = true ;
              } // if 
              
              ch = G.sIn.ReadChar() ;
        } // while 
        
        return order ;
        
  } // Skipspace()
  
  
  Boolean 後為p() throws Throwable {
        
        char ch = '\0' ;
        ch = G.sIn.ReadChar() ; // 看是否為p
        while ( ch == '\n' || ch == ' ' ) {
              ch = G.sIn.ReadChar() ; // 讀到p為止
        } // while
        
        if ( ch == 'p' ) {
              return true ;
        } // if
        else {
              return false ;
        } // else
        
  } // 後為p()
  
   Boolean L後為b() throws Throwable {
        
        char ch = '\0' ;
        ch = G.sIn.ReadChar() ; // 看是否為b
        while ( ch == '\n' || ch == ' ' ) {
              ch = G.sIn.ReadChar() ;// 讀到b為止
        } // while
        
        if ( ch == 'b' ) {
              return true ;
        } // if
        else {
              return false ;
        } // else
        
  } // L後為b()
   
   Boolean N後為b() throws Throwable {
         
         char ch = '\0' ;
         ch = G.sIn.ReadChar() ; // 看是否為b
         while ( ch == '\n' || ch ==  ' ' ) {
               ch = G.sIn.ReadChar() ; // 讀到b為止
         } // while
         
         if ( ch == 'b' ) {
               return true ;
         } // if 
         else {
               return false ;
         } // else 
         
   } // N後為b()
   
   Boolean Isdigit( String str ) throws Throwable {
         for ( int i = 0 ; i < str.length() ; i++ ) {
               int chr = str.charAt(i) ;
               if ( chr > 48 && chr < 57 ) {
                     return true ;
               } // if 
         } // for
         
         return false ;
         
   } // Isdigit()
   
   static int DigitInString( String str ) throws Throwable {
         String str2 = new String() ;
         for ( int i = 0 ; i < str.length() ; i ++ ) {
               if ( str.charAt(i) >= 48 && str.charAt(i) <= 57 ) {
                     str2 = str2 + str.charAt(i) ; // str2裡是數字 String型別
               } // if 
         } // for
         
         int i = Integer.valueOf( str2 ) ;
         return i ;
         
   } // DigitInString()
   
   
              
  public  void main ( String[] args ) throws Throwable {

    G.Init() ;
    G.sTestNum = G.sIn.ReadInt() ; // 測試數據
    G.CYPrint( "歡迎使用中原資工Line editor ...\n" ) ; 
    
    Editor allbuffer = new Editor() ; // 初始化
    allbuffer.初始化() ; // 初始化物件才能用
    // -----------------------------------------------------------
    String word = new String() ;
    word = G.sIn.ReadString() ;
    
    while ( word.compareTo( "q" ) != 0 ) {
          
          System.out.print( "> " ) ;
          if ( word. compareTo( "a") == 0 || word.compareTo( ".a")  == 0 ) {
                char ch = '\0' ; 
                ch = G.sIn.ReadChar() ;
                while ( ch != '\n' ) {
                      ch = G.sIn.ReadChar () ;
                } // while 
                
                allbuffer.sbuffer.get( allbuffer.snum ).ADD() ;                                                                                                  // 加入-----，get(): 取出buffer
          } // if
          else if ( word.equals( "i" ) || word.equals ( ".i" ) ) allbuffer.sbuffer.get( allbuffer.snum ).INSERT() ;       // 插入----
          else if ( word.equals( "c" ) || word.equals ( ".c" ) ) allbuffer.sbuffer.get( allbuffer.snum ).CHANGE() ;  // 交換----
          else if ( word.equals( "d" ) || word.equals ( ".d" ) ) allbuffer.sbuffer.get( allbuffer.snum ).DELETE() ;   // 刪除----
          else if ( word.equals( "p" ) || word.equals( "." ) ) { // 如果是p/.印現在行~
               if ( allbuffer.sbuffer.get( allbuffer.snum ).mline.isEmpty() ) {
                     System.out.println( "Error : There is no data! Please try again." ) ;
               } // if 
               else if ( word.compareTo( "p" ) == 0 ) {
                     G.CYPrint( allbuffer.sbuffer.get( allbuffer.snum ).mline.get( allbuffer.sbuffer.get(allbuffer.snum).mlinenum - 1 ) + "\n" )  ;
               } // else if     
               else if ( word.compareTo( "." ) ==   0 ) { 
                     System.out.print( allbuffer.sbuffer.get( allbuffer.snum ).mlinenum+ ":"  ) ;
                     G.CYPrint( allbuffer.sbuffer.get( allbuffer.snum ).mline.get( allbuffer.sbuffer.get( allbuffer.snum ).mlinenum-1 ) + "\n" ) ;
               } // else if 
               else if ( word.contains( "%" ) ) { // 印全部--------------遇到%%%%%要往下讀看有無P------------
                   
                    if ( word.compareTo( "%p" ) == 0 ) {
                          allbuffer.sbuffer.get( allbuffer.snum ).PRINTALL() ;
                    } // if 
                    else {
                        
                          if ( 後為p() ) {
                                allbuffer.sbuffer.get( allbuffer.snum ).PRINTALL() ;
                          } // if 
                        
                    } // else
                 } // else if 
            } // else if 
             else if ( Isdigit( word ) ) // 判斷這個String是否數字，如果是就討論裡面的數字------------
                   allbuffer.sbuffer.get( allbuffer.snum ).數字( word ) ;
             else if ( word.contains( "$" ) ) //有$號的話，設為最後一行----$$$$$$$$$$$------
                   allbuffer.sbuffer.get( allbuffer.snum) .遇到$( word ) ;
             else if ( word.contains( "b" ) ) allbuffer.處理( word ) ;
                     // 如果有b的話，------------bbbbbbbbb-------------
             else if ( word.contains( "n" ) && word.length() == 1 ) if ( N後為b() ) Editor.產生() ; // 讀到n往下，有換行
             else if ( word.contains( "l" ) && word.length() == 1 ) if ( L後為b() ) Editor.順序列出() ; // 讀到l往下抓，有換行
             else {       // 其他---------------
                   System.out.println( "Error : illegal command! Please try again." ) ;
             } // else
             
             word = G.sIn.ReadString() ;// 拿來讀英文符號
             
        } // while
                   
          

        
        
        
        

  } //  main 
  
} // Main1


  


