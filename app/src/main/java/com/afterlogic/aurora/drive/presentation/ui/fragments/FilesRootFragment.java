package com.afterlogic.aurora.drive.presentation.ui.fragments;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.core.util.ObjectUtil;
import com.afterlogic.aurora.drive.core.util.interfaces.OnBackPressedListener;
import com.afterlogic.aurora.drive.data.common.api.Api;
import com.afterlogic.aurora.drive.data.common.api.ApiCallback;
import com.afterlogic.aurora.drive.data.common.api.ApiError;
import com.afterlogic.aurora.drive.data.common.api.ApiResponseError;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.ui.FilesListActivity;
import com.afterlogic.aurora.drive.presentation.ui.common.views.DisablableViewPager;
import com.annimon.stream.Stream;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by sashka on 31.03.16.
 * mail: sunnyday.development@gmail.com
 */
public class FilesRootFragment extends Fragment implements
        View.OnClickListener,
        FilesCallback,
        OnBackPressedListener,
        SwipeRefreshLayout.OnRefreshListener
{
    public static final String EXTRA_IS_ROOT_FOLDER =
            FilesRootFragment.class.getName() + ".EXTRA_IS_ROOT_FOLDER";
    private static final String AVAILABLE_TYPES =
            FilesTypesAdapter.class.getName() + ".AVAILABLE_TYPES";
    public static final String FAB_ACTIONS =
            FilesTypesAdapter.class.getName() + ".FAB_ACTIONS";

    //UI references
    private TabLayout mTabLayout;
    private DisablableViewPager mViewPager;
    private FloatingActionsMenu mAddMenu;

    private FilesTypesAdapter mFilesTypesAdapter;
    private SwipeRefreshLayout mRefreshLayout;
    private int mCurrentFragment;

    private final List<String> mAvailableTypes = new ArrayList<>();
    private final Set<String> mTypesInCheck = new HashSet<>();
    private final HashMap<String, FilesListFragment> mFirstInstances = new HashMap<>();

    private FilesRootFragmentCallback mCallback;
    private boolean mIsRoot = true;
    private boolean mIsMultiChoise = false;
    private boolean mFabVisible = true;
    private boolean mIsError = false;
    private int mActualCheckRequestId = 0;

    public static FilesRootFragment newInstance(boolean fabActions) {

        Bundle args = new Bundle();
        args.putBoolean(FAB_ACTIONS, fabActions);
        FilesRootFragment fragment = new FilesRootFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mCallback = (FilesRootFragmentCallback) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getAvailableFolders(savedInstanceState);
        mFilesTypesAdapter = new FilesTypesAdapter(mAvailableTypes, getContext(), getChildFragmentManager());

        if (getArguments() != null){
            mFabVisible = getArguments().getBoolean(FAB_ACTIONS, true);
        }

        //setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_files_root, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTabLayout = mCallback.getFilesTabLayout();

        if (savedInstanceState != null){
            mIsRoot = savedInstanceState.getBoolean(EXTRA_IS_ROOT_FOLDER, true);
        }

        //[START Init FAB menu]
        mAddMenu = (FloatingActionsMenu) view.findViewById(R.id.add_menu);
        if (mFabVisible) {
            view.findViewById(R.id.create_folder).setOnClickListener(this);
            view.findViewById(R.id.upload_file).setOnClickListener(this);
            view.findViewById(R.id.fab_collapser).setOnTouchListener((v, event) -> {
                if (mAddMenu.isExpanded()) {
                    mAddMenu.collapse();
                    return true;
                }
                return false;
            });
        }
        //[END Init FAB menu]

        mViewPager = (DisablableViewPager) view.findViewById(R.id.types_pager);
        mViewPager.setAdapter(mFilesTypesAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mAddMenu.collapse();
            }

            @Override
            public void onPageSelected(int position) {
                mCurrentFragment = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mCurrentFragment = mViewPager.getCurrentItem();

        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        mRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent);
        mRefreshLayout.setOnRefreshListener(this);

        updateCheckingUI();
        updateUI();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(EXTRA_IS_ROOT_FOLDER, mIsRoot);
        if (mTypesInCheck.size() == 0) {
            ArrayList<String> types = new ArrayList<>();
            types.addAll(mAvailableTypes);
            outState.putStringArrayList(AVAILABLE_TYPES, types);
        }
    }

    @Override
    public boolean onBackPressed() {
        if (mFilesTypesAdapter != null) {
            FilesListFragment current = mFilesTypesAdapter.getFilesFragment(mCurrentFragment);
            return current != null && current.onBackPressed();
        }else{
            return false;
        }
    }

    /**
     * {@link View.OnClickListener#onClick(View)}  implementation.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.create_folder:
                mFilesTypesAdapter.getFilesFragment(mCurrentFragment)
                        .requestCreateFolder();
                break;
            case R.id.upload_file:
                FilesListFragment fragment = mFilesTypesAdapter.getFilesFragment(mViewPager.getCurrentItem());
                AuroraFile folder = fragment.getCurrentFolder();
                mCallback.requestFileUpload(folder);
                break;
        }
        mAddMenu.collapse();
    }

    /**
     * {@link FilesCallback#onOpenFolder(AuroraFile)}  implementation.
     */
    @Override
    public void onOpenFolder(AuroraFile folder) {
        if (mViewPager == null) return;
        mIsRoot = "".equals(folder.getFullPath());
        updateUI();
        if (mAddMenu != null){
            mAddMenu.collapse();
        }
    }

    /**
     * {@link FilesCallback#showActions(AuroraFile)}  implementation.
     */
    @Override
    public void showActions(AuroraFile file) {
        //STUB. It is handled in Activity.
    }

    /**
     * {@link FilesCallback#onFileClicked(AuroraFile, List)}  implementation.
     */
    @Override
    public void onFileClicked(AuroraFile file, List<AuroraFile> allFiles) {
        //STUB. It is handled in Activity.
    }

    @Override
    public void createFolder(String path, String type, String folderName) {
        //STUB. It is handled in Activity.
    }

    /**
     * {@link SwipeRefreshLayout.OnRefreshListener#onRefresh()}  implementation.
     */
    @Override
    public void onRefresh() {
        getAvailableFolders(null);
    }

    ////////////////////////////////////////////////
    // [START Own methods] // <editor-fold desc="Own methods">
    ////////////////////////////////////////////////

    public void setMultichoiseMode(boolean multichoiseMode){
        if (mViewPager != null){
            FilesListFragment flf = mFilesTypesAdapter.getFilesFragment(mViewPager.getCurrentItem());
            flf.setMultichoiseMode(multichoiseMode);
            mIsMultiChoise = multichoiseMode;
            updateUI();
        }
    }

    /**
     * Get current visible folder.
     * @return - current folder or null if not have available folders or they not initialized.
     */
    @Nullable
    public AuroraFile getCurrentFolder(){
        if (mViewPager != null){
            if (mFilesTypesAdapter.getCount() > 0){
                FilesListFragment files = mFilesTypesAdapter.getFilesFragment(
                        mViewPager.getCurrentItem()
                );
                if (files != null){
                    return files.getCurrentFolder();
                }
            }
        }
        return null;
    }

    /**
     * Get current {@link FilesListFragment}
     * @return - current selected fragment fragment.
     */
    public FilesListFragment getCurrentListFragment(){
        if (mViewPager != null){
            if (mFilesTypesAdapter.getCount() > 0){
                return mFilesTypesAdapter.getFilesFragment(
                        mViewPager.getCurrentItem()
                );
            }
        }
        return null;
    }

    /**
     * Get available folder types.
     * @param savedState - if not null try restore types from saved state.
     */
    private void getAvailableFolders(Bundle savedState){
        mActualCheckRequestId++;
        mAvailableTypes.clear();
        //Restore
        if (savedState != null){
            List<String> availableTypes = savedState.getStringArrayList(AVAILABLE_TYPES);
            if (availableTypes != null && availableTypes.size() > 0){
                mAvailableTypes.addAll(availableTypes);
                return;
            }
        }

        //[START Reset values]
        mTypesInCheck.clear();
        if (mFilesTypesAdapter != null) {
            mFilesTypesAdapter.notifyDataSetChanged();
        }
        mIsError = false;
        //Update UI
        updateCheckingUI();
        //[END Reset values]

        mTypesInCheck.addAll(getAllAvailableTypes());

        //Check types
        Stream.of(mTypesInCheck).forEach(type -> checkType(type, mActualCheckRequestId));
    }

    private void checkType(String type, final int requestId){
        Api.getFiles(AuroraFile.parse("", type, true), null, new ApiCallback<List<AuroraFile>>() {
            @Override
            public void onSucces(List<AuroraFile> result) {
                if (requestId != mActualCheckRequestId) return;

                //On success create fragment with result content
                //It will be showed in adapter at first time.
                FilesListFragment fragment = FilesListFragment.newInstance(type, null, result);
                //Add data to buffers
                mFirstInstances.put(type, fragment);
                mAvailableTypes.add(type);

                endCheck(type);
            }

            @Override
            public void onError(ApiError error) {
                if (requestId != mActualCheckRequestId) return;

                if (error.getCode() != ApiResponseError.RESULT_FALSE){
                    //Increase request id for prevent handling all other responses
                    mActualCheckRequestId++;
                    //Set error indicator
                    mIsError = true;
                    mFirstInstances.clear();
                }

                endCheck(type);
            }
        });
    }

    /**
     * Handle check folder type task notifyEnd.
     */
    private void endCheck(String type){

        mTypesInCheck.remove(type);

        if (!isAdded()) return;

        if (mIsError){
            if (getActivity() != null){
                FilesListActivity activity = (FilesListActivity) getActivity();
                activity.showOfflineState();
                return;
            }
        }

        //Check is all tasks notifyEnd
        if (mTypesInCheck.size() == 0){

            updateCheckingUI();
            updateUI();

            List<String> allTypes = getAllAvailableTypes();
            Collections.sort(mAvailableTypes, (f, s) -> allTypes.indexOf(f) - allTypes.indexOf(s));

            //notify adapter with instantiated files fragments
            mFilesTypesAdapter.notifyChangedWithInstances(mFirstInstances);
            //Clear buffers
            mFirstInstances.clear();

            if (getActivity() != null){
                FilesListActivity activity = (FilesListActivity) getActivity();
                activity.onAvailableFilesTypesChecked();
            }
        }
    }

    /**
     * Refresh {@link FilesListFragment} with requested type.
     * @param type - type of folder (personal, corporate and etc.)
     */
    public void refreshFolderByType(String type){
        if (mFilesTypesAdapter != null) {
            for (int i = 0; i < mFilesTypesAdapter.getCount(); i++){
                FilesListFragment fragment = mFilesTypesAdapter.getFilesFragment(i);
                if (fragment != null){
                    if (type != null) {
                        AuroraFile itemFolder = fragment.getCurrentFolder();
                        if (type.equals(itemFolder.getType())) {
                            fragment.refreshCurrentFolder();
                        }
                    } else {
                        fragment.refreshCurrentFolder();
                    }
                }
            }
        }
    }

    /**
     * Refresh selected {@link FilesListFragment}.
     */
    public void refreshCurrentFolder(){
        if (mFilesTypesAdapter != null && mCurrentFragment >= 0 && mCurrentFragment < mFilesTypesAdapter.getCount()) {
            FilesListFragment fragment = mFilesTypesAdapter.getFilesFragment(mCurrentFragment);
            if (fragment != null){
                fragment.refreshCurrentFolder();
            }
        }
    }

    public void refreshAllFolders(){
        final FilesTypesAdapter adapter = mFilesTypesAdapter;
        if (adapter != null && mTypesInCheck.size() == 0){
            Stream.of(0, adapter.getCount())
                    .map(adapter::getFilesFragment)
                    .filter(ObjectUtil::nonNull)
                    .forEach(FilesListFragment::refreshCurrentFolder);
        }
    }

    /**
     * Handle file deleting.
     * @param files - deleted files.
     */
    public void onFileDeleted(List<AuroraFile> files){
        if (mFilesTypesAdapter != null) {
            for (int i = 0; i < mFilesTypesAdapter.getCount(); i++){
                FilesListFragment fragment = mFilesTypesAdapter.getFilesFragment(i);
                if (fragment != null){
                    fragment.onFilesDeleted(files);
                }
            }
        }
    }

    private void updateCheckingUI(){
        if (mViewPager != null) {
            mRefreshLayout.setEnabled(mIsError);
            mRefreshLayout.post(() -> mRefreshLayout.setRefreshing(mTypesInCheck.size() > 0));
        }
    }

    /**
     * Update visibility changeable UI.
     */
    protected void updateUI(){
        if (mTabLayout != null) {
            mTabLayout.setVisibility(
                    mIsRoot && mAvailableTypes.size() > 1 && !mIsMultiChoise ?
                            View.VISIBLE : View.GONE);
        }
        if (mViewPager != null) {
            mViewPager.setSwipeEnabled(mIsRoot && ! mIsMultiChoise);
        }
        if (mAddMenu != null) {
            mAddMenu.setVisibility(
                    mAvailableTypes.size() > 0 && mFabVisible && !mIsMultiChoise ?
                            View.VISIBLE : View.GONE);
        }
    }

    public void notifyDataSetChanged(){
        if (mFilesTypesAdapter != null){
            mFilesTypesAdapter.notifyFragmentsDataChanged();
        }
    }

    private List<String> getAllAvailableTypes(){
        return Arrays.asList(getResources().getStringArray(R.array.folder_types));
    }

    ////////////////////////////////////////////////
    // [END Own methods] // </editor-fold>
    ////////////////////////////////////////////////

    ////////////////////////////////////////////////
    // [START Classes] // <editor-fold desc="Classes">
    ////////////////////////////////////////////////

    public interface FilesRootFragmentCallback {
        TabLayout getFilesTabLayout();
        void requestFileUpload(AuroraFile folder);
    }

    /**
     * Two files folder types adapter. Personal or Corporate {@link FilesListFragment}s.
     */
    private class FilesTypesAdapter extends FragmentPagerAdapter {

        private final HashMap<String, FilesListFragment> mFirstInstances = new HashMap<>();
        private SparseArray<FilesListFragment> mFilesListFragments = new SparseArray<>();
        private List<String> mTypes;
        private Resources mResources;

        public FilesTypesAdapter(@NonNull List<String> types, Context ctx, FragmentManager fm) {
            super(fm);
            mTypes = types;
            mResources = ctx.getResources();
        }

        @Override
        public Fragment getItem(int position) {
            FilesListFragment f = null;
            String type = mTypes.get(position);
            if (mFirstInstances != null) {
                f = mFirstInstances.get(type);
                mFirstInstances.remove(type);
            }
            if (f == null){
                f = FilesListFragment.newInstance(type);
            }
            return f;
        }

        /**
         * Add Fragment to local fragments array on item init.
         * Need for getting fragment at {@link #getFilesFragment(int)}.
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            FilesListFragment fragment =
                    (FilesListFragment) super.instantiateItem(container, position);

            mFilesListFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public int getCount() {
            return mTypes.size();
        }

        @Override
        public int getItemPosition(Object object) {
            if (mTypes.size() == 0) return POSITION_NONE;
            return super.getItemPosition(object);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String[] titles = mResources.getStringArray(R.array.folder_names);
            String[] types = mResources.getStringArray(R.array.folder_types);

            for (int i = 0; i < types.length; i++){
                if (types[i].equals(mTypes.get(position))){
                    return titles[i];
                }
            }

            return null;
        }

        /**
         * Set first instances of {@link FilesListFragment}s and {@link #notifyDataSetChanged()}.
         * @param firstInstances - Map of first instances of files fragment with type key.
         *                       It will be cleared after {@link #getItem(int)}.
         */
        public void notifyChangedWithInstances(HashMap<String, FilesListFragment> firstInstances){
            mFirstInstances.clear();
            mFirstInstances.putAll(firstInstances);
            notifyDataSetChanged();
        }

        /**
         * Get {@link FilesListFragment} by position.
         */
        public FilesListFragment getFilesFragment(int position) {
            return mFilesListFragments.get(position);
        }

        public void notifyFragmentsDataChanged(){
            for (int i = 0; i < mFilesListFragments.size(); i ++){
                mFilesListFragments.valueAt(i).notifyDataSetChanged();
            }
        }
    }

    ////////////////////////////////////////////////
    // [END Classes] // </editor-fold>
    ////////////////////////////////////////////////
}
