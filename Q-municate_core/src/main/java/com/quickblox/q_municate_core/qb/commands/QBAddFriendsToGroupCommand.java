package com.quickblox.q_municate_core.qb.commands;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.quickblox.chat.model.QBDialog;
import com.quickblox.q_municate_core.core.command.ServiceCommand;
import com.quickblox.q_municate_core.qb.helpers.QBGroupChatHelper;
import com.quickblox.q_municate_core.service.QBService;
import com.quickblox.q_municate_core.service.QBServiceConsts;
import com.quickblox.q_municate_core.utils.ChatUtils;
import com.quickblox.q_municate_db.managers.DatabaseManager;

import java.util.ArrayList;

public class QBAddFriendsToGroupCommand extends ServiceCommand {

    private QBGroupChatHelper multiChatHelper;

    public QBAddFriendsToGroupCommand(Context context, QBGroupChatHelper chatHelper, String successAction,
            String failAction) {
        super(context, successAction, failAction);
        this.multiChatHelper = chatHelper;
    }

    public static void start(Context context, String dialogId, ArrayList<Integer> friendIdsList) {
        Intent intent = new Intent(QBServiceConsts.ADD_FRIENDS_TO_GROUP_ACTION, null, context,
                QBService.class);
        intent.putExtra(QBServiceConsts.EXTRA_DIALOG_ID, dialogId);
        intent.putExtra(QBServiceConsts.EXTRA_FRIENDS, friendIdsList);
        context.startService(intent);
    }

    @Override
    public Bundle perform(Bundle extras) throws Exception {
        String dialogId = extras.getString(QBServiceConsts.EXTRA_DIALOG_ID);
        ArrayList<Integer> friendIdsList = (ArrayList<Integer>) extras.getSerializable(
                QBServiceConsts.EXTRA_FRIENDS);

        QBDialog dialog = multiChatHelper.addUsersToDialog(dialogId, friendIdsList);

        if (dialog != null) {
            DatabaseManager.getInstance().getDialogManager().createOrUpdate(ChatUtils.createLocalDialog(dialog));
        }

        Bundle returnedBundle = new Bundle();
        returnedBundle.putSerializable(QBServiceConsts.EXTRA_DIALOG, dialog);
        return returnedBundle;
    }
}