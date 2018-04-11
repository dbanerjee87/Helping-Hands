/*
 * Created by Itzik Braun on 12/3/2015.
 * Copyright (c) 2015 deluge. All rights reserved.
 *
 * Last Modification at: 3/12/15 4:27 PM
 */

package co.chatsdk.ui.Resources;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import co.chatsdk.core.session.NM;
import co.chatsdk.core.session.StorageManager;
import co.chatsdk.core.dao.DaoCore;
import co.chatsdk.core.dao.Thread;
import co.chatsdk.core.dao.User;
import co.chatsdk.core.utils.DisposableList;
import co.chatsdk.ui.R;
import co.chatsdk.ui.main.BaseActivity;
import co.chatsdk.ui.main.BaseFragment;
import io.reactivex.Completable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by itzik on 6/17/2014.
 */
public class resourcesFragment extends BaseFragment  {

    /** Loading all the current user contacts.*/
    public static final int MODE_LOAD_CONTACTS = 1991;

    /** Loading all users for given thread id mode*/
    public static final int MODE_LOAD_THREAD_USERS = 1992;

    /** Using the users that was given to the fragment in to initializer;*/
    public static final int MODE_USE_SOURCE = 1995;

    public static final int MODE_LOAD_CONTACT_THAT_NOT_IN_THREAD = 1996;

    /** When a user clicked he will be added to the current thread.*/
    public static final int CLICK_MODE_ADD_USER_TO_THREAD = 2991;
    /** Used for the share intent, When a user press on a user the attached bundle from the share intent will be sent to the selected user.*/
    public static final int CLICK_MODE_SHARE_CONTENT = 2992;
    /** Open profile context when user is clicked.*/
    public static final int CLICK_MODE_SHOW_PROFILE = 2993;
    /** Nothing happen on list item click.*/
    public static final int CLICK_MODE_NONE = 2994;

    public static final String LOADING_MODE = "Loading_Mode";
    public static final String CLICK_MODE = "Click_Mode";
    public static final String IS_DIALOG = "Is_Dialog";

    /** The text color that the adapter will use, Use -1 to set adapter to default color.*/
    protected int textColor = -1991;

    protected ProgressBar progressBar;
    protected RecyclerView recyclerView;

    protected Disposable listOnClickListenerDisposable;

    private DisposableList disposables = new DisposableList();

    /** Users that will be used to fill the adapter, This could be set manually or it will be filled when loading users for
     * {@link #loadingMode}*/
    protected List<User> sourceUsers = new ArrayList<>();

    /** Used when the fragment is shown as a dialog*/
    protected String title = "";

    /** Determine which users will be loaded to this fragment.
     *
     * @see
     *  #MODE_LOAD_CONTACT_THAT_NOT_IN_THREAD,
     *  #MODE_LOAD_CONTACTS
     *  #MODE_LOAD_FOLLOWERS
     *  #MODE_LOAD_FOLLOWS
     *  #MODE_LOAD_THREAD_USERS
     *  #MODE_USE_SOURCE
     *  */
    protected int loadingMode;

    /** Determine what happen after a user is clicked.
     *
     * @see
     * #CLICK_MODE_ADD_USER_TO_THREAD
     * #CLICK_MODE_SHARE_CONTENT
     * #CLICK_MODE_SHOW_PROFILEs */
    protected int clickMode;

    protected Object extraData ="";

    /** Set to false if you dont want any menu item to be inflated for this fragment.
     *  This should be set before the fragment transaction,
     *  if you extends the fragment you can call it in {@link #onCreate(android.os.Bundle)}
     *  <B>see </B>{@link #setInflateMenu(boolean inflate)}*/
    protected boolean inflateMenu = true;

    /** When isDialog = true the dialog will always show the list of users given to him or pulled by the thread id.*/
    private boolean isDialog = false;

    public static resourcesFragment newInstance() {
        resourcesFragment f = new resourcesFragment();
        f.setLoadingMode(MODE_LOAD_CONTACTS);
        Bundle b = new Bundle();
        f.setArguments(b);
        return f;
    }

    public static resourcesFragment newInstance(int loadingMode, int clickMode, Object extraData) {
        resourcesFragment f = new resourcesFragment();
        f.setLoadingMode(loadingMode);
        f.setClickMode(clickMode);
        f.setExtraData(extraData);
        return f;
    }

    /** Creates a new contact dialog.
     * @param threadID - The id of the thread that his users is the want you want to show.
     * @param title - The title of the dialog.
     */
    public static resourcesFragment newThreadUsersDialogInstance(String threadID, String title) {
        resourcesFragment f = new resourcesFragment();
        f.setTitle(title);

        f.setLoadingMode(MODE_LOAD_THREAD_USERS);
        f.setDialog();
        f.setExtraData(threadID);
        Bundle b = new Bundle();
        f.setArguments(b);
        return f;
    }

    public void setDialog(){
        this.isDialog = true;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setLoadingMode(int loadingMode){
        this.loadingMode = loadingMode;
    }

    public void setExtraData(Object extraData){
        this.extraData = extraData;
    }

    public void setClickMode(int clickMode) {
        this.clickMode = clickMode;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null)
        {
            loadingMode = savedInstanceState.getInt(LOADING_MODE);
            clickMode = savedInstanceState.getInt(CLICK_MODE);
            isDialog = savedInstanceState.getBoolean(IS_DIALOG);
        }

        if (!isDialog) {
            setHasOptionsMenu(true);
            setRetainInstance(true);
        }



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (isDialog) {
            if(title.equals("")) {
                getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
            }
            else {
                getDialog().setTitle(title);
            }
        }

        mainView = inflater.inflate(R.layout.chat_sdk_resources, null);

        //initViews();


        return mainView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(LOADING_MODE, loadingMode);
        outState.putBoolean(IS_DIALOG, isDialog);
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        if (!inflateMenu)
            return;

        MenuItem item =
                menu.add(Menu.NONE, R.id.action_chat_sdk_add, 10, "Add Chat");
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        item.setIcon(R.drawable.ic_plus);
    }





    private Completable reloadUsers () {
        return Completable.create(e -> {
            if (loadingMode != MODE_USE_SOURCE) {

                sourceUsers.clear();
                // If this is not a dialog we will load the contacts of the user.
                switch (loadingMode) {
                    case MODE_LOAD_CONTACTS:
                        sourceUsers.addAll(NM.contact().contacts());
                        Timber.d("Contacts: " + sourceUsers.size());
                        break;

                    case MODE_LOAD_THREAD_USERS:
                        Thread thread = DaoCore.fetchEntityWithEntityID(Thread.class, extraData);

                        // Remove the current user from the list.
                        List<User> users = thread.getUsers();
                        users.remove(NM.currentUser());

                        sourceUsers.addAll(users);
                        break;

                    case MODE_LOAD_CONTACT_THAT_NOT_IN_THREAD:
                        List<User> users1 = NM.contact().contacts();
                        thread = StorageManager.shared().fetchThreadWithID((Long) extraData);
                        List<User> threadUser = thread.getUsers();
                        users1.removeAll(threadUser);
                        sourceUsers.addAll(users1);
                        break;
                }
            }
            e.onComplete();
        }).subscribeOn(Schedulers.single());
    }

    @Override
    public void onResume() {
        super.onResume();
        //loadData(true);
    }

    @Override
    public void reloadData() {
        reloadUsers().subscribe();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    public void setInflateMenu(boolean inflateMenu) {
        this.inflateMenu = inflateMenu;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        disposables.dispose();
    }
    @Override
    public void clearData() {

    }

}
