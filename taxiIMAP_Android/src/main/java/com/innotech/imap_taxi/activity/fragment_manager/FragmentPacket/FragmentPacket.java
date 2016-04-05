package com.innotech.imap_taxi.activity.fragment_manager.FragmentPacket;

import android.support.v4.app.Fragment;

/**
 * This class is holder for app fragments parameters.
 */
public abstract class FragmentPacket extends Fragment {
    private int id;
    private int backFragment = 11;
    public static final int SWIPE = 0;
    public static final int ETHEAR = 2;
    public static final int PARKINGS = 3;
    public static final int ORDER = 4;
    public static final int ORDER_DETAILS = 5;
    public static final int MAP = 6;
    public static final int ORDERS = 7;
    public static final int CURRENTORDERS = 8;
    public static final int ARCHIV = 9;
    public static final int TAXOMETR = 10;
    public static final int TAXOMETR_ClASSES_TARIF = 11;
    public static final int TAXOMETR_TARIFS = 12;
    public static final int TAXOMETR_ACCEPT_TARIF = 13;
    public static final int OWN_ORDER = 14;
    public static final int GET_ADDRESS = 15;
    public static final int PARKING_CARD_FRAGMENT = 16;

    public FragmentPacket(int id) {
        super();
        this.id = id;
    }

   /* private FragmentManager.OnBackStackChangedListener getListener()
    {
        FragmentManager.OnBackStackChangedListener result = new FragmentManager.OnBackStackChangedListener()
        {
            public void onBackStackChanged()
            {
                FragmentManager manager = getFragmentManager();

                if (manager != null)
                {
                    if(manager.getBackStackEntryCount() >= 1){
                        String topOnStack = manager.getBackStackEntryAt(manager.getBackStackEntryCount()-1).getName();
                        Log.i("TOP ON BACK STACK",topOnStack);
                    }
                    }
                }
        };

        return result;
    }*/
    
    public int getIdFragment() {
        return id;
    }

    public int getBackFragment() {
        return backFragment;
    }

    public void setBackFragment(int backFragment) {
        this.backFragment = backFragment;
    }
    
}
