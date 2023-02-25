package com.github.klee0kai.hummus.adapterdelegates.delegate;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.github.klee0kai.hummus.adapterdelegates.delegate.viewbinding.ClassViewBindingAdapterDelegate;
import com.github.klee0kai.hummus.adapterdelegates.delegate.viewbinding.ViewBindingHolder;
import com.github.klee0kai.hummus.databinding.ItemLabelHorizontalBinding;
import com.github.klee0kai.hummus.interfaces.ICallBackResult;

public class AdapterDelegates {

    public static <T> ClassViewBindingAdapterDelegate<T, ItemLabelHorizontalBinding> labelDelegate(
            Class<T> tClass,
            ICallBackResult<T, String> provideString,
            View.OnClickListener click,
            View.OnLongClickListener longClick
    ) {
        return new ClassViewBindingAdapterDelegate<T, ItemLabelHorizontalBinding>(tClass) {
            @Override
            protected ItemLabelHorizontalBinding onCreateViewBinding(LayoutInflater inflater, ViewGroup parent) {
                return ItemLabelHorizontalBinding.inflate(inflater, parent, false);
            }

            @Override
            protected void onBindViewHolder(T it, int pos, @NonNull ViewBindingHolder<ItemLabelHorizontalBinding> vh) {
                vh.binding.label.setText(provideString.call(it));

                vh.itemView.setOnClickListener(click);
                vh.itemView.setOnLongClickListener(longClick);
            }
        };
    }

    public static <T> ClassViewBindingAdapterDelegate<T, ItemLabelHorizontalBinding> labelDelegate(
            Class<T> tClass,
            ICallBackResult<T, String> provideString) {
        return labelDelegate(tClass, provideString, null, null);
    }

    public static ClassViewBindingAdapterDelegate<String, ItemLabelHorizontalBinding> stringDelegate(
            View.OnClickListener click,
            View.OnLongClickListener longClick
    ) {
        return new ClassViewBindingAdapterDelegate<String, ItemLabelHorizontalBinding>(String.class) {
            @Override
            protected ItemLabelHorizontalBinding onCreateViewBinding(LayoutInflater inflater, ViewGroup parent) {
                return ItemLabelHorizontalBinding.inflate(inflater, parent, false);
            }

            @Override
            protected void onBindViewHolder(String it, int pos, @NonNull ViewBindingHolder<ItemLabelHorizontalBinding> vh) {
                vh.binding.label.setText(it);

                vh.itemView.setOnClickListener(click);
                vh.itemView.setOnLongClickListener(longClick);
            }
        };
    }

    public static ClassViewBindingAdapterDelegate<String, ItemLabelHorizontalBinding> stringDelegate() {
        return stringDelegate(null, null);
    }


}
