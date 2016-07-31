#include <NewPing.h>

#define echoPin 3 // Echo Pin
#define trigPin 4 // Trigger Pin
#define transDelay 25

/*
 * SOUND SPEED (cm per microsecond)
 */
const float SOUND_SPEED = 343.2 * 100 / 1000000;


int maximumRange = 3000; // Maximum range needed
int minimumRange = 0; // Minimum range needed
long duration, distance = -1; // Duration used to calculate distance
NewPing sonar(trigPin, echoPin, 300);

void setup() {
 Serial.begin (9600);
 pinMode(trigPin, OUTPUT);
 pinMode(echoPin, INPUT);
}

void loop() {
/* The following trigPin/echoPin cycle is used to determine the
 distance of the nearest object by bouncing soundwaves off of it. */ 
 
 //Calculate the distance (in cm) based on the speed of sound.
 int duration = sonar.ping_median(4);
 distance = sonar.convert_cm(duration); 

 Serial.print('#');
 if (distance >= maximumRange || distance <= minimumRange){
   /* Send a negative number to computer and Turn LED ON 
   to indicate "out of range" */
   Serial.print("-1");
 }
 else {
   Serial.print(distance);
 }
 Serial.print('~');
 Serial.println();
 
 //Delay 50ms before next reading.
 delay(transDelay);
}
