#include <android/log.h>
#include "AudioEngine.h"
#include <thread>
#include <mutex>

#define TWO_PI (3.14159 * 2)
#define AMPLITUDE 0.5

constexpr int32_t kBufferSizeInBursts = 2;
float FREQUENCY = 650;
AAudioStreamBuilder *streamBuilder;


aaudio_data_callback_result_t dataCallback(
        AAudioStream *stream,
        void *userData,
        void *audioData,
        int32_t numFrames) {

    ((AudioEngine *) (userData))->render(static_cast<float *>(audioData), numFrames);
    return AAUDIO_CALLBACK_RESULT_CONTINUE;
}

bool AudioEngine::start(float frequency) {
    FREQUENCY = frequency;
    AAudio_createStreamBuilder(&streamBuilder);
    AAudioStreamBuilder_setFormat(streamBuilder, AAUDIO_FORMAT_PCM_FLOAT);
    AAudioStreamBuilder_setChannelCount(streamBuilder, 1);
    AAudioStreamBuilder_setPerformanceMode(streamBuilder, AAUDIO_PERFORMANCE_MODE_LOW_LATENCY);
    AAudioStreamBuilder_setDataCallback(streamBuilder, ::dataCallback, &audioEngine_);
    AAudioStreamBuilder_setErrorCallback(streamBuilder, ::errorCallback, this);
    aaudio_result_t result = AAudioStreamBuilder_openStream(streamBuilder, &stream_);
    if (result != AAUDIO_OK) {
        __android_log_print(ANDROID_LOG_ERROR, "AudioEngine", "Error opening stream %s",
                            AAudio_convertResultToText(result));
        return false;
    }
    int32_t sampleRate = AAudioStream_getSampleRate(stream_);
    setSampleRate(sampleRate, frequency);
    AAudioStream_setBufferSizeInFrames(
    stream_, AAudioStream_getFramesPerBurst(stream_) * kBufferSizeInBursts);
    result = AAudioStream_requestStart(stream_);
    if (result != AAUDIO_OK) {
        __android_log_print(ANDROID_LOG_ERROR, "AudioEngine", "Starting stream error",
                            AAudio_convertResultToText(result));
        return false;
    }

    return true;
}

bool AudioEngine::updateSampleRate(float frequency){
    try {
        int32_t sampleRate = AAudioStream_getSampleRate(stream_);
        setSampleRate(sampleRate, frequency);
        // Sets buffer size.
        AAudioStream_setBufferSizeInFrames( stream_, AAudioStream_getFramesPerBurst(stream_) * kBufferSizeInBursts);
        // Starts audio stream.
        aaudio_result_t result = AAudioStream_requestStart(stream_);
        if (result != AAUDIO_OK) {
            __android_log_print(ANDROID_LOG_ERROR, "AudioEngine", "Starting stream error", AAudio_convertResultToText(result));
            return false;
        }
    }
    catch (int mynum) {
        __android_log_print(ANDROID_LOG_ERROR, "AudioEngine", "Update sample rate error");
    }
}

void AudioEngine::stop() {
    if (stream_ != nullptr) {
        AAudioStream_requestStop(stream_);
        AAudioStream_close(stream_);
    }
}

void AudioEngine::restart(){
    static std::mutex restartingLock;
    if (restartingLock.try_lock()){
        stop();
        start(FREQUENCY);
        restartingLock.unlock();
    }
}

void AudioEngine::setToneOn(bool isToneOn) {
    setWaveOn(isToneOn);
}

void AudioEngine::setSampleRate(int32_t sampleRate, float frequency) {
    phaseIncrement_ = (TWO_PI * frequency) / (double) sampleRate;
}

void AudioEngine::setWaveOn(bool isWaveOn) {
    isWaveOn_.store(isWaveOn);
}

bool AudioEngine::getSineOn(){
    return isSineOn_.load();
}

void AudioEngine::setSineOn(bool isSineOn){
    isSineOn_.store(isSineOn);
}

void AudioEngine::setAmp(float amp){
    amp_.store(amp);
}

void AudioEngine::render(float *audioData, int32_t numFrames) {
    if (!isWaveOn_.load()) phase_ = 0;
    for (int i = 0; i < numFrames; i++) {
        if (isWaveOn_.load()) {
            float waveForm = (float) (phase_ * amp_);
            if (isSineOn_.load()){
                waveForm = (float) (sin(phase_) * amp_);
            }
            audioData[i] = waveForm;
            phase_ += phaseIncrement_;
            if (phase_ > TWO_PI) phase_ -= TWO_PI;
        } else {
            audioData[i] = 0;
        }
    }
}

void errorCallback(AAudioStream *stream, void *userData, aaudio_result_t error){
    if (error == AAUDIO_ERROR_DISCONNECTED){
        std::function<void(void)> restartFunction = std::bind(&AudioEngine::restart, static_cast<AudioEngine *>(userData));
        new std::thread(restartFunction);
    }
}

