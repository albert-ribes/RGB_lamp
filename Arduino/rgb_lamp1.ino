//RGB lamp

int ledR = 3;
int ledG = 5;
int ledB = 6;

char newInput;
boolean stringComplete=false;
int blueSwitch=0;
String inputString="";
String serialString="";

int resistorValueR=0, resistorValueG=0, resistorValueB=0;
float voltageR=0.0, voltageG=0.0, voltageB=0.0;
String bluetoothR="", bluetoothG="", bluetoothB="";
float pwmR=0.0, pwmG=0.0, pwmB=0.0;

void setup(){
  pinMode(8, INPUT);      //blueSwitch
  pinMode(A1, INPUT);     //Input resistor Red
  pinMode(A2, INPUT);     //Input resistor Green
  pinMode(A3, INPUT);     //Input resistor Blue
  
  pinMode(7, OUTPUT);     //blueLED
  pinMode(ledR, OUTPUT);  //PWM Red
  pinMode(ledG, OUTPUT);  //PWM Green
  pinMode(ledB, OUTPUT);  //PWM Blue
  
  Serial.begin(9600);
//  inputString.reserve(200);
}

void loop()
{
  blueSwitch =digitalRead(8);
  digitalWrite(7,blueSwitch);

  resistorValueR = analogRead(A1);
  resistorValueG = analogRead(A2);
  resistorValueB = analogRead(A3);
  voltageR = resistorValueR*(5.0/1023.0);
  voltageG = resistorValueG*(5.0/1023.0);
  voltageB = resistorValueB*(5.0/1023.0);
  
  if (stringComplete){
    stringComplete=false;
    serialString="Analog voltage R: "; 
    serialString.concat(voltageR);
    serialString.concat(", G: ");
    serialString.concat(voltageG);
    serialString.concat(", B: ");
    serialString.concat(voltageB);
        
    Serial.println(serialString);
    Serial.println("BlueSwitch: " + blueSwitch);
    Serial.println("Input string: " + inputString);
    
    String str="";
    int j=0;
    for (int i=0;i<=inputString.length();i++){
          if (inputString[i] != ','){
            //Serial.print(inputString[i]);
            if (j==0){
              bluetoothR+=inputString[i];
            }
            else if (j==1){
              bluetoothG+=inputString[i];
            }
            else if (j==2){
              bluetoothB+=inputString[i];
            }
          }
          else{
            j++;
          }
        }
    Serial.print("digitalR: " + bluetoothR);
    Serial.print(", digitalG: " + bluetoothG);
    Serial.print(", digitalB: " + bluetoothB);
      if (blueSwitch == HIGH){
        pwmR=bluetoothR.toFloat();
        pwmG=bluetoothG.toFloat();
        pwmB=bluetoothB.toFloat();
      }
      else{
        pwmR=resistorValueR/4.0;
        pwmG=resistorValueG/4.0;
        pwmB=resistorValueB/4.0;
      }
      inputString="";
      bluetoothR="";
      bluetoothG="";
      bluetoothB="";
      Serial.println("pwmR: " + String(pwmR) + ", pwmG:" + String(pwmG) + ", pwmB:" + String(pwmB));
  }  
   if (blueSwitch == LOW){
      pwmR=resistorValueR/4.0;
      pwmG=resistorValueG/4.0;
      pwmB=resistorValueB/4.0;
    }
  
  //Serial.println("pwmR: " + String(pwmR) + ", pwmG:" + String(pwmG) + ", pwmB:" + String(pwmB));
  analogWrite(ledR,pwmR);
  analogWrite(ledG,pwmG);
  analogWrite(ledB,pwmB);
  
}

void serialEvent(){
    while (Serial.available()){
      char inChar=(char)Serial.read();
      inputString+=inChar;
      if ((inChar=='\r')||(inChar=='\n')){
        stringComplete=true;
        
       }
    }
}
