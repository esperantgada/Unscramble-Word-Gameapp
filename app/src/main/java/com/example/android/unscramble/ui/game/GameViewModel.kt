package com.example.android.unscramble.ui.game

import android.text.Spannable
import android.text.SpannableString
import android.text.style.TtsSpan
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {


    //Hold a list of words you use in the game, to avoid repetitions.
    private var wordList : MutableList<String> = mutableListOf()

    //Hold the word the player is trying to unscramble
    private lateinit var currentWord : String

    /**[_currentScrambleWorld] Is accessible and editable only in this class
     * [currrentScrambledWorld] is only readble in GameFragment
     */
/*
    private  val _currentScrambledWord = MutableLiveData<String>()
     val currentScrambledWord : LiveData<String>
    get() = _currentScrambledWord
*/

    /**
     * A better user experience would be to have Talkback read aloud the individual characters of
     * the scrambled word. Within this [viewModel] class, we're going to convert the scrambled word String to a
     * [Spannable] string. A [spannable] string is a string with some extra information attached to it.
     * In this case, we want to associate the string with a [TtsSpan] of [TYPE_VERBATIM], so that the
     * [text-to-speech] engine reads aloud the scrambled word verbatim, character by character.
     */
    private  val _currentScrambledWord = MutableLiveData<String>()
    val currentScrambledWord : LiveData<Spannable> = Transformations.map(_currentScrambledWord){
        if (it == null){
            SpannableString("")
        }else{
            val scrambledWord = it.toString()
            val spannable : Spannable = SpannableString(scrambledWord)
            spannable.setSpan(
                TtsSpan.VerbatimBuilder(scrambledWord).build(),0, scrambledWord.length,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            spannable
        }
    }

    private val _currentWordCount = MutableLiveData<Int>(0)
           val currentWordCount : LiveData<Int>
    get() = _currentWordCount

    private val _score = MutableLiveData<Int>(0)
    val score: LiveData<Int>
        get() = _score

    /**
    *Updates currentWord and currentScrambledWord with the next word.
    */
    private fun getNextWord() {
        currentWord = allWordsList.random()
        val tempWord = currentWord.toCharArray()
        tempWord.shuffle()

        // Continue the loop until the scrambled word is not the same as the original word.
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
        getNextWord()
    }

    /**
    * Returns true if the current word count is less than MAX_NO_OF_WORDS and
    * updates the next word.
    */
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


    /**Validate the player's word**/
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