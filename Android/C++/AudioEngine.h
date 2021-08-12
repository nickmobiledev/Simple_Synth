#ifndef PART1_AUDIOENGINE_H
#define PART1_AUDIOENGINE_H

#include <aaudio/AAudio.h>
#include <atomic>

class AudioEngine {
public:
    void stop();
    void restart();
    void setToneOn(bool isToneOn);
    bool start(float frequency);
    bool updateSampleRate(float frequency);
    void setWaveOn(bool isWaveOn);
    void setSineOn(bool isSineOn);
    bool getSineOn();
    void setAmp(float amp);
    void setSampleRate(int32_t sampleRate, float frequency);
    void render(float *audioData, int32_t numFrames);
private:
    AudioEngine *audioEngine_;
    AAudioStream *stream_;
    std::atomic<bool> isWaveOn_{false};
    std::atomic<bool> isSineOn_{false};
    std::atomic<float> amp_{0.5};
    double phase_ = 0.0;
    double phaseIncrement_ = 0.0;
};
