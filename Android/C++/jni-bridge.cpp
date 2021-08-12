#include <jni.h>
#include <android/input.h>
#include "AudioEngine.h"

static AudioEngine *audioEngine = new AudioEngine();
static AudioEngine *audioEngines[100] = {new AudioEngine()};
static int audioEngineListSize = 1;

extern "C" {
JNIEXPORT void JNICALL
Java_com_example_wavemaker_MainActivity_toneOn(JNIEnv *env, jobject /* this */, jboolean onOrOff, jint position) {
    audioEngines[position]->setToneOn(onOrOff);
}
JNIEXPORT void JNICALL
Java_com_example_wavemaker_MainActivity_sineOn(JNIEnv *env, jobject /* this */, jboolean onOrOff, jint position) {
    audioEngines[position]->setSineOn(onOrOff);
}
JNIEXPORT jboolean JNICALL
Java_com_example_wavemaker_MainActivity_getSineOn(JNIEnv *env, jobject /* this */, jint position) {
    return audioEngines[position]->getSineOn();
}
JNIEXPORT void JNICALL
Java_com_example_wavemaker_MainActivity_setAmp(JNIEnv *env, jobject /* this */, jint position, jfloat amp) {
    audioEngines[position]->setAmp(amp);
}
JNIEXPORT void JNICALL
Java_com_example_wavemaker_MainActivity_startEngine(JNIEnv *env, jobject /* this */, float frequency, jint position) {
    audioEngines[position]->start(frequency);
}
JNIEXPORT void JNICALL
Java_com_example_wavemaker_MainActivity_stopEngine(JNIEnv *env, jobject /* this */, jint position) {
    audioEngines[position]->stop();
}
JNIEXPORT void JNICALL
Java_com_example_wavemaker_MainActivity_updateFrequency(JNIEnv *env, jobject thiz, jfloat frequency, jint position) {
    audioEngines[position]->updateSampleRate(frequency);
}
JNIEXPORT void JNICALL
Java_com_example_wavemaker_MainActivity_addEngine(JNIEnv *env, jobject thiz) {
    audioEngines[audioEngineListSize] = new AudioEngine();
    audioEngineListSize++;
}
}
