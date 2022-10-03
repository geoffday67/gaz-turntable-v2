#include <Arduino.h>
#include <AccelStepper.h>

#define STEPPER_BLUE_PIN      6
#define STEPPER_PINK_PIN      7
#define STEPPER_YELLOW_PIN    8
#define STEPPER_ORANGE_PIN    9
#define STEPPER_STEPS_PER_REVOLUTION  2048

#define SENSOR_INPUT          A0
#define READINGS_PER_SENSOR   10

#define SENSORS               3
#define BITS_PER_SENSOR       2
#define SENSOR_START          10

AccelStepper stepper(AccelStepper::FULL4WIRE, STEPPER_BLUE_PIN, STEPPER_YELLOW_PIN, STEPPER_PINK_PIN, STEPPER_ORANGE_PIN);

int sensorIndex = 0;

// Multiple readings per sensor so we can compute a rolling average.
int sensors[SENSORS][READINGS_PER_SENSOR];
int values[SENSORS];

void setup() {
  int n;

  Serial.begin(115200);
  Serial.println("Starting");

  for (n = 0; n < BITS_PER_SENSOR; n++) {
    pinMode(SENSOR_START + n, OUTPUT);
  }
  Serial.println("Sensors initialised");

  stepper.setSpeed(600.0);
  stepper.setMaxSpeed(600.0);
  stepper.setAcceleration(1000.0);
  Serial.println("Motor initialised");
}

void setAddress(int address) {
  int n;
  byte mask = 0x01;

  for (n = 0; n < BITS_PER_SENSOR; n++, mask <<= 1) {
    digitalWrite(SENSOR_START + n, address & mask);
  }
}

void readAll() {
  int n, i, total;

  // Get the latest readings.
  for (n = 0; n < SENSORS; n++) {
    setAddress(n);
    delay(20);
    sensors[n][sensorIndex] = analogRead(SENSOR_INPUT);
  }

  if (++sensorIndex == READINGS_PER_SENSOR) {
    sensorIndex = 0;
  } 

  // Compute the average over all the readings held.
  for (n = 0; n < SENSORS; n++) {
    total = 0;
    for (i = 0; i < READINGS_PER_SENSOR; i++) {
        total += sensors[n][i];
    }
    values[n] = total / READINGS_PER_SENSOR;
  }
}

int previous(int n) {
  if (--n < 0) {
    return SENSORS - 1;
  } else {
    return n;
  }
}

int next(int n) {
  if (++n == SENSORS) {
    return 0;
  } else {
    return n;
  }
}

void process() {
  int n;

  // Find the largest value.
  int max = -1, largest;
  for (n = 0; n < SENSORS; n++) {
    if (values[n] > max) {
      max = values[n];
      largest = n;
    }
  }
  Serial.print("Largest:");
  Serial.println(largest);

  // Find the larger of the two neighbours.
  int left = previous(largest);
  int right = next(largest);
  double gap = 100.0 / (SENSORS - 1);
  double position = 0.0;
  if (values[left] > values[right]) {
  } else {
    Serial.print("Neighbour:");
    Serial.println(right);

    // Find the ratio between the values of the largest and its right-hand neighbour.
    double ratio = (double) values[right] / (values[largest] + values[right]);
    position = (gap * largest) + (gap * ratio);
  }

  Serial.print("Position:");
  Serial.println(position);
}

void loop() {
  int n;

    /*stepper.runToNewPosition(0);
    stepper.runToNewPosition(500);
    stepper.runToNewPosition(100);
    stepper.runToNewPosition(120);*/


  readAll();

  Serial.print("Level:");
  Serial.print(values[0]);
  Serial.print(",");
  Serial.print(values[1]);
  Serial.print(",");
  Serial.print(values[2]);
  Serial.println();

  for (n = 0; n < SENSORS; n++) {
    if (values[n] > 0) {
      process();
      break;
    }
  }

  //delay(100);
}