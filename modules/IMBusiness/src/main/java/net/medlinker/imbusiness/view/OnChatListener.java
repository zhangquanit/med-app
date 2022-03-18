package net.medlinker.imbusiness.view;

/**
 * @author hmy
 * @time 2020/10/14 22:32
 */
public interface OnChatListener {
    boolean onSendMsgIntercept();

    void onSendText(String text);

    void onGirdMenuShow();
}
