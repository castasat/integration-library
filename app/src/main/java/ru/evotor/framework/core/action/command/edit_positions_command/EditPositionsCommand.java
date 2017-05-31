package ru.evotor.framework.core.action.command.edit_positions_command;

import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;

import java.util.List;
import java.util.Objects;

import ru.evotor.framework.Utils;
import ru.evotor.framework.core.ICanStartActivity;
import ru.evotor.framework.core.IntegrationManagerCallback;
import ru.evotor.framework.core.IntegrationManagerImpl;
import ru.evotor.framework.core.action.datamapper.ChangesMapper;
import ru.evotor.framework.core.action.event.receipt.changes.IChange;
import ru.evotor.framework.core.action.event.receipt.changes.position.IPositionChange;

/**
 * Created by a.kuznetsov on 26/04/2017.
 */

public class EditPositionsCommand {

    public static final String NAME = "evo.v2.receipt.sell.editPositions";
    private static final String KEY_CHANGES = "changes";

    public static EditPositionsCommand create(Bundle bundle) {
        Parcelable[] changesParcelable = bundle.getParcelableArray(KEY_CHANGES);
        return new EditPositionsCommand(Utils.filterByClass(
                ChangesMapper.INSTANCE.create(changesParcelable),
                IPositionChange.class
        ));
    }

    private final List<IPositionChange> changes;

    public EditPositionsCommand(List<IPositionChange> changes) {
        this.changes = changes;
    }

    public void process(final Context context, final ICanStartActivity activityStarter, IntegrationManagerCallback callback) {
        Objects.requireNonNull(context);
        Objects.requireNonNull(activityStarter);

        List<ComponentName> componentNameList = IntegrationManagerImpl.convertImplicitIntentToExplicitIntent(NAME, context.getApplicationContext());
        if (componentNameList == null || componentNameList.isEmpty()) {
            return;
        }
        new IntegrationManagerImpl(context.getApplicationContext())
                .call(EditPositionsCommand.NAME,

                        componentNameList.get(0),
                        this.toBundle(),
                        activityStarter,
                        callback,
                        new Handler(Looper.getMainLooper())
                );
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        Parcelable[] changesParcelable = new Parcelable[changes.size()];
        for (int i = 0; i < changesParcelable.length; i++) {
            IChange change = changes.get(i);
            changesParcelable[i] = ChangesMapper.INSTANCE.toBundle(change);
        }
        bundle.putParcelableArray(KEY_CHANGES, changesParcelable);
        return bundle;
    }

    public List<IPositionChange> getChanges() {
        return changes;
    }
}
