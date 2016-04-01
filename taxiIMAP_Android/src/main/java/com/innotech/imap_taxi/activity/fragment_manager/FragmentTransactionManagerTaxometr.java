package com.innotech.imap_taxi.activity.fragment_manager;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import android.util.Log;
import com.innotech.imap_taxi.activity.fragment_manager.FragmentPacket.ClassiTarifov;
import com.innotech.imap_taxi.activity.fragment_manager.FragmentPacket.FragmentPacket;
import com.innotech.imap_taxi.activity.fragment_manager.FragmentPacket.TarifAccept;
import com.innotech.imap_taxi.activity.fragment_manager.FragmentPacket.Tarifs;
import com.innotech.imap_taxi.activity.fragment_manager.FragmentPacket.TaxometrFragment;
import com.innotech.imap_taxi.helpers.ContextHelper;
import com.innotech.imap_taxi3.R;

public class FragmentTransactionManagerTaxometr {

	private FragmentTransaction fragmentTransaction;
	private FragmentActivity fragmentActivity;
	private static FragmentTransactionManagerTaxometr instance;
	private ArrayList<FragmentPacket> stack;
	private int id = 11;
	private FragmentTransactionManagerTaxometr() {

		stack = new ArrayList<FragmentPacket>();
		stack.add(new ClassiTarifov());
		stack.add(new Tarifs());
		stack.add(new TarifAccept());
		stack.add(new TaxometrFragment());
	} 
	public static FragmentTransactionManagerTaxometr getInstance() {

		if(instance == null) {
			instance = new FragmentTransactionManagerTaxometr();
		}
		return instance;
	}

	public void initializationFragmentTransaction(FragmentActivity activity) {
		this.fragmentActivity = activity;
		fragmentTransaction = fragmentActivity.getSupportFragmentManager().beginTransaction();
		for(Fragment f : stack) {
			fragmentTransaction.add(R.id.fgrmContTaxoMetr, f);
			fragmentTransaction.hide(f);
		}
		id = id != 0 ? id : stack.get(0).getIdFragment();
        for(FragmentPacket fr : stack) {
            if(fr.getIdFragment() == id) {
                fragmentTransaction.show(fr);
                fr.onResume();
            } else {
                fragmentTransaction.hide(fr);
            }
        }

		commit();
	}

    public void remove (FragmentActivity activity){
        this.fragmentActivity = activity;
        fragmentTransaction = fragmentActivity.getSupportFragmentManager().beginTransaction();
        for (Fragment f : stack){
            fragmentTransaction.remove(f);
        }
        commit();
    }

	public void openFragment(int id) {
		fragmentTransaction = fragmentActivity.getSupportFragmentManager().beginTransaction();

		for(FragmentPacket f : stack) {
			if(f.getIdFragment()==id) {
				f.setBackFragment(this.id);

				for(FragmentPacket fr : stack) {
					if(fr.getIdFragment() == id) {
						fragmentTransaction.show(fr);
						fr.onResume();
					} else {
						fragmentTransaction.hide(fr);
					}
				}
				this.id = id;
				break;
			}
		}

		commit();
	}

	public void back() {
		if (this.id!=FragmentPacket.TAXOMETR){
            if(this.id != FragmentPacket.TAXOMETR_ClASSES_TARIF){
                fragmentTransaction = fragmentActivity.getSupportFragmentManager().beginTransaction();
                for(FragmentPacket f : stack) {
                    if(f.getIdFragment() == id) {
                        id = f.getBackFragment();
                        if (id == 0){                                                   //свежие правки
                            ContextHelper.getInstance().getCurrentActivity().finish();
                        }                                                               //свежие правки
                        fragmentTransaction.hide(f);
                        for(FragmentPacket fr : stack) {
                            if(fr.getIdFragment() == id) {
                                fragmentTransaction.show(fr);
                                break;
                            }
                        }
                    }
                }
            }else {
                ContextHelper.getInstance().getCurrentActivity().finish();
            }
		}
		commit();
	}

	private void commit() {
		try{	
			fragmentTransaction.commit();
			//fragmentTransaction.commitAllowingStateLoss();
		}
		catch (Exception e){e.printStackTrace();}
	} 

	public int getId() {
		return this.id;
	}



}
