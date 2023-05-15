class ButtonStateLiveData : LiveData<Boolean>() {
    // Método para alterar o estado do botão
    fun setButtonState(enabled: Boolean) {
        value = enabled
    }
}
