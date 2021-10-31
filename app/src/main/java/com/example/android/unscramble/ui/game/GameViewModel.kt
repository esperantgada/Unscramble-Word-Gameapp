package com.example.android.unscramble.ui.game

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {


    private var wordList : MutableList<String> = mutableListOf()
    private lateinit var currentWord : String

    /**_currentScrambleWorld Is accessible and editable only in this class
     * currrentScrambledWorld is only readble in GameFragment
     */
    private  var _currentScrambledWord = MutableLiveData<String>()
     val currentScrambledWord : LiveData<String>
    get() = _currentScrambledWord

    private var _currentWordCount = MutableLiveData<Int>(0)
           val currentWordCount : LiveData<Int>
    get() = _currentWordCount

    private var _score = MutableLiveData<Int>(0)
    val score: LiveData<Int>
        get() = _score

    /**
    *Updates currentWord and currentScrambledWord with the next word.
    */
    private fun getNextWord() {
        currentWord = allWordsList.random()
        val tempWord = currentWord.toCharArray()
        tempWord.shuffle()

        while (String(tempWord).equals(currentWord, false)) {
            tempWord.shuffle()
        }


        if (wordList.contains(currentWord)) {
            getNextWord()
        } else {
            _currentScrambledWord.value = String(tempWord)
            _currentWordCount.value = (_currentWordCount.value)?.inc()
            wordList.add(currentWord)
        }
    }

    init {
        Log.d("GameFragment", "GameViewModel Created")
        getNextWord()
    }

    fun nextWord(): Boolean {
        return if (_currentWordCount.value!! < MAX_NO_OF_WORDS) {
            getNextWord()
             true
        } else
            false
    }

    /**Increases the score**/
    private fun increaseScore(){
        _score.value = (_score.value)?.plus(SCORE_INCREASE)

    }


    /**Validate the player word**/
    fun isPLayerWordCorrect(playerWord : String) : Boolean{
        if (playerWord.equals(currentWord, true)) {
            increaseScore()
            return true
        }
        return false

    }

    /**reinitialize data and restart the game**/
    fun reinitializedData(){
        _score.value = 0
        _currentWordCount.value = 0
        wordList.clear()
        getNextWord()
    }



}