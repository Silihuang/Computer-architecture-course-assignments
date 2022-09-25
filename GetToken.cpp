/* 
 * File:   main.cpp
 * Author: chien yu
 *
 * Created on 2017年4月16日, 上午 12:40
 */

#include <stdlib.h>
#include <iostream>
#include <string.h>
#include <stdio.h>
using namespace std;

enum TokenType {
  IDENTIFIER = 34512, CONSTANT = 87232, SPECIAL = 29742
};

typedef char * CharPtr;

struct Column {
  int column; // 此token(的字首)在本line有出現於此column
  Column * next; // 下一個此token(在本line)有出現的column
}; // Column

typedef Column * ColumnPtr;

struct Line { // struct Line 記錄了4種資訊
  int line; // 此token放在哪一 line
  ColumnPtr firstAppearAt; // 指到此token第一次出現在此line的哪一column
  ColumnPtr lastAppearAt; // 指到此token最後出現在此line的哪一column
  Line * next; // 同一 token 下一次出現在哪一 line
};

typedef Line * LinePtr;

struct Token { // Struct token 紀錄了4種資訊
  CharPtr tokenStr; // tokenStr 放你切下來的token
  TokenType type; // type 紀錄此token是屬於哪一個case
  LinePtr firstAppearOn; // 此 token 第一次出現在哪一 line
  LinePtr lastAppearOn; // 此 token 最後出現在哪一 line
  Token * next; // 指向下一個token
}; // struct Token

typedef Token * TokenPtr;
TokenPtr gFront = NULL, gRear = NULL; // 分別指向Token串列的頭跟尾
typedef char Str100[ 100 ]; // 此型別是打算用來宣告一個陣列變數、以供讀字串之用
int gLine = 1; // 「下一個要讀進來的字元」所在的line number
int gColumn = 1; // 「下一個要讀進來的字元」所在的column number
int gfirstcolumn = 1;

void Star( char & word ) {
  if ( word == '\n' ) gLine++;
  if (word == '*') {
    if ((word = getchar()) == '/');
    else return Star(word);
  }// if

  else return Star(word = getchar());
} // Star()

int Case(char word) {
  if (word == ' ' || word == '\n' || word == '\t') return 40;
  if ((word >= 'a' && word <= 'z') || (word >= 'A' && word <= 'Z') || word == '_')
    return 10;
  if ((word >= '0' && word <= '9')) return 20;
  if (word == '=' || word == '>' || word == '<' || word == '!') return 31;
  if ( word == '"' ) return 21;
  if (word == '+') return 32;
  if (word == '-') return 33;
  if (word == '&') return 34;
  if (word == '|') return 35;
  else return 30;
} // Case()

int New( Str100 & t, char & word, int n) {  // 會進來代表 case < 40
  gfirstcolumn = gColumn;
  t[0] = word;
  int i = 1;
  if (word == '"') {
    for (; (word = getchar()) != '"'; gColumn++) {
      t[i] = word;
      i++;
      // cout << "1: " << word << "\n" ;
    } // for
    
    t[i] = word;
    gColumn++;
  }// if

  else if ( word == '/' ) {
    // cout << "2: " << word << "\n" ;
    if ((word = getchar()) == '/') {
      while ((word = getchar()) != '\n');
      return 0 ;
    } // if
    
    else if (word == '*') {
      Star(word = getchar());
      return 0 ;
    } // else if
    
    else {
      gColumn++;
      return 2;
    } // if // 多讀掉一個
  }// else if
  
    // ( word = getchar()) != '\n' && ( word = getchar() )!= ' ' && ( word = getchar() ) != '\t'
  else { // ( word != ' ' && word != '\n' && word != '\t' )
    // cout << "3: " << word << "\n" ;
    word = getchar();
    for (  gColumn++ ; word != '\n' && word != ' ' &&
            word != '\t'; gColumn++ ) {
      if (  n != 30 && (
              (n == 20 && ( Case(word) == 20 || word == '.')) ||
              (n == 10 && ( Case(word) == 10 || Case(word) == 20)) ||
              (n == 33 && (word == '-' || word == '>')) || Case(word) == n  ) ) {
        t[i] = word;
        i++;
        word = getchar();
        // cout << t << " " << n <<" 有近來i++\n" ;
      }// if
      
      else return 2; // 多讀掉一個
    } // for
    
  } // else 

  return 1;
} // New()

void Newcolumn(ColumnPtr & first, ColumnPtr & last) {
  if (first == NULL) {
    first = new Column;
    first->column = gfirstcolumn;
    first->next = NULL;
    last = first;
    // cout << "column : " << first->column << "\n";
  }// if

  else Newcolumn(first->next, last);
} // Newcolumn()

void Newline(LinePtr & first, LinePtr & last) {
  if (first == NULL) {
    first = new Line;
    first->line = gLine; // 此token放在哪一 line
    first->firstAppearAt = NULL; // 指到此token第一次出現在此line的哪一column
    first->lastAppearAt = NULL; // 指到此token最後出現在此line的哪一column
    Newcolumn(first->firstAppearAt, first->lastAppearAt);
    first->next = NULL; // 同一 token 下一次出現在哪一 line
    last = first;
    // cout << "line : " << first->line << "\n";
  }// if 

  else if (first->line == gLine)
    Newcolumn(first->firstAppearAt, first->lastAppearAt);
  else Newline(first->next, last);
} // Newline()

void Newtoken(TokenPtr & previous, char * token, TokenType type, TokenPtr & head) {
  TokenPtr temp = head;
  previous = new Token;
  previous->tokenStr =  token ;
  // cout << token << "\n";
  previous->type = type;
  previous->firstAppearOn = NULL;
  previous->lastAppearOn = NULL;
  Newline(previous->firstAppearOn, previous->lastAppearOn);
  previous->next = temp;
  // previous->next = head;
  head = previous;
} // Newtoken()

TokenPtr gprevious = NULL;

void Addtoken(TokenPtr & head, char * token, TokenType type, bool & repeat ) {
  if (head != NULL && strcmp(head->tokenStr, token) == 0) {
    Newline(head->firstAppearOn, head->lastAppearOn );
    gprevious = NULL;
    repeat = true;
  }// if
  else if (head != NULL && strcmp(head->tokenStr, token) < 0) {
    gprevious = head;
    Addtoken( head->next, token, type, repeat);
  }// else if

  else {
    if (gprevious == NULL) Newtoken(gprevious, token, type, head); // 最前
    else Newtoken(gprevious->next, token, type, head); //中間.最後
    if (head == NULL) gRear = gprevious;
    gprevious = NULL;
  } // else

} // Addtoken()

void Printcolumn( LinePtr temp1, ColumnPtr temp2 ) {
  if ( temp2 != NULL ) {
    cout << "(" << temp1->line << "," ;
    cout << temp2->column << ")";
    Printcolumn( temp1, temp2->next ) ;
  } // if
  
  else return ;
} // Printcolumn()

void Printline( LinePtr temp ) {
  if ( temp != NULL ) {
    Printcolumn( temp, temp->firstAppearAt ) ;
    Printline( temp->next) ;
  } // if
  
  else return ;
} // Printline()

bool Sameline ( LinePtr temp, int line ) {
  if ( temp != NULL ) {
    if ( temp->line == line ) {
      // cout << "進來了!! line == " << line << "\n";
      return true ;
    } // if
    else return Sameline ( temp->next, line ) ;
  } // if
  
  else return false ;
} // Sameline()

void Printsameline ( TokenPtr temp, int line ) {
  if ( temp != NULL ) {
    bool yes = Sameline ( temp->firstAppearOn, line);
    if ( yes ) cout << temp->tokenStr << "\n" ;
    Printsameline ( temp->next, line ) ;
  } // if
  
  else  return ;
} // viod()

void Printsametoken ( TokenPtr temp, CharPtr token ){
  if ( temp != NULL && strcmp ( temp->tokenStr, token ) == 0 ) {
    Printline( temp->firstAppearOn ) ;
    cout << "\n";
    return ;
  } // if
  
  if ( temp != NULL && strcmp ( temp->tokenStr, token ) != 0 ) 
    Printsametoken (temp->next,token);
  else {
    cout << "查無此token : " << token ;
    return ;
  } // else
} // Printsametoken()

void Printtoken( TokenPtr temp ) {
  if ( temp != NULL ) {
    cout << temp->tokenStr << " " ;
    Printline( temp->firstAppearOn ) ;
    cout << "\n";
    Printtoken( temp->next ) ;
  } // if
  
  else return ;
} // Printtoken()

int main() { // IDENTIFIER = 34512, CONSTANT = 87232, SPECIAL = 29742
  char word = ' ';
  int num1 = 0, num2 = 0, num3 = 0;
  int process = 0; 
  Str100 token = { 0 };
  word = getchar(); // gColumn++ C++ 讀字元的語法 
  for (   ; strcmp( token, "END_OF_FILE" ) != 0 ; ) {
    for ( int i = 0 ; i != 100 ; i++ ) token[i] = '\0';
    TokenType type;
    if ( Case(word) == 10 ) {
      process = New( token, word, Case(word) );
      type = IDENTIFIER;
      if ( strcmp( token, "END_OF_FILE" ) != 0 ) num1++;
      // cout << "num1: " << token << " " << num1 << "\n";
    }// if
 
    else if ( Case( word ) < 30 ) {
      process = New(token, word, Case( word ) );
      type = CONSTANT;
      num2++;
      // cout << "num2: " << token << " " << num2 << "\n";
    }// else if

    else if ( Case( word ) < 40 ) {
      process = New(token, word, Case( word ) );
      type = SPECIAL;
      if ( process != 0 ) num3++;
     // cout << "num3: " << token << " " << num3 << process << "\n";
    } // else if

    
    if ( strcmp( token, "END_OF_FILE" ) != 0 && process >  0 ) {
      bool repeat = false ;
      char * t = new char[ strlen(token) + 1 ];
      // cout << "token : " << token << "\n";
      strcpy(t, token);
      // cout << "t : " << t << "\n";
      Addtoken( gFront, t, type, repeat);
      if ( repeat ) {
        if ( Case( t[0] ) == 10 ) num1--;
        else if ( Case( t[0] ) < 30 ) num2--;
        else if ( Case( t[0] ) < 40 ) num3--;
      } // if
      
      process--; // 1 or 2 -> 0 or 1
    } // else if

    // if ( word == ' ' ) gColumn++;
    if (word == '\n') {
      // cout << "line++ : " << gLine << " " << gColumn << "\n" ;
      gLine++;
      gColumn = 0;
    } // if
    
    if ( strcmp( token, "END_OF_FILE" ) != 0 &&
          ( word == ' ' || word == '\n' || word == '\t' || process == 0 ) ) {
      word = getchar();
      gColumn++;
    } // if
  } // for

  
  Printtoken( gFront ) ;

  cout << "\n請輸入指令：\n" << "1.總共有多少種 token\n" << "2.三種case各有多少 token\n"
          << "3.印出指定 token 的位置 (line number,column number) (要排序)\n"
          << "4.印出所指定的那一 line 出現哪些 token (要排序)\n"
          << "5.結束\n\n";


  
    while ( cin >> word ) {
    if (word == '1') cout << "總共" << num1 + num2 + num3 << "種\n\n";
    if (word == '2') cout << "Case1 共 " << num1 << "個\n" << "Case2 共 "
            << num2 << "個\n" << "Case3 共 " << num3 << "個\n\n";
    if (word == '3') {
      cout << "請輸入要搜尋的 token :\n";
      cin >> token;
      cout << token << " " ;
      Printsametoken ( gFront, token );
      cout << "\n";
    } // if
    
    if (word == '4') {
      cout << "請輸入要指定的 line :\n";
      int line ;
      cin >> line ;
      Printsameline ( gFront,  line );
      cout << "\n";
    } // if

    if ( word == '5' ) {
      cout << "byebye";
      return 0;
    } // if

  } // while

}

