package com.xiaoma.bluetooth.phone.contacts.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.bluetooth.phone.R;
import com.xiaoma.bluetooth.phone.common.utils.ContactNameUtils;
import com.xiaoma.bluetooth.phone.contacts.model.ContactSection;

import java.util.List;


/**
 * Created by qiuboxiang on 2018/12/4 20:07
 */
public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {
    private List<ContactSection> items;
    private boolean isShowCollection = true;
    private OnItemChildClickListener listener;

   /* public ContactAdapter(int layoutResId, int sectionHeadResId, List data) {
        super(layoutResId, sectionHeadResId, data);
    }*/

    public ContactAdapter(List<ContactSection> item) {
        this.items = item;
    }

   /* @Override
    protected void convertHead(BaseViewHolder helper, final ContactSection items) {
        helper.setText(R.id.tv_head, items.header);
    }*/

   /* @Override
    protected void convert(BaseViewHolder helper, ContactSection items) {
        ContactBean bean = items.t;
        helper.setText(R.id.contact_name, ContactNameUtils.getLimitedContactName(bean.getName()))
//                .setText(R.id.phone_num, bean.getPhoneNum())
                .addOnClickListener(R.id.image)
                .getView(R.id.image).setSelected(items.t.isCollected());
        helper.setVisible(R.id.image, isShowCollection);
        helper.setVisible(R.id.divide, !bean.isLastOne());

//        CircleCharAvatarView mIvHead = helper.getView(R.id.head);
//        OperateUtils.setHeadImage(mIvHead, bean);
    }*/

     public int getPositionForSection(char c) {
         try {
             for (int i = 0; i < items.size(); i++) {
                 ContactSection section = items.get(i);
                 if (section == null || !section.isHeader) {
                     continue;
                 }
                 String header = section.header;
                 char firstChar = header.toUpperCase().charAt(0);
                 if (firstChar == c) {
                     return i;
                 }
             }
         } catch (Exception e) {
             e.printStackTrace();
         }
         return -1;
     }

    public void isShowCollection(boolean isShow) {
        isShowCollection = isShow;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        ContactSection contactSection = items.get(position);
        if (contactSection.t == null) return;
        if (contactSection.isHeader) {
            holder.letterParent.setVisibility(View.VISIBLE);
            holder.letter.setText(contactSection.header);
        } else {
            holder.letterParent.setVisibility(View.GONE);
        }
        holder.name.setText(ContactNameUtils.getLimitedContactName(contactSection.t.getName()));
        holder.img.setSelected(contactSection.t.isCollected());

        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemChildClick(position);
                }
            }
        });

        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.OnItemClickListener(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View letterParent;
        private TextView letter;
        private TextView name;
        private ImageView img;
        private ImageView divide;

        public ViewHolder(View itemView) {
            super(itemView);
            letterParent = itemView.findViewById(R.id.letter_parent);
            letter = itemView.findViewById(R.id.letter);
            name = itemView.findViewById(R.id.contact_name);
            img = itemView.findViewById(R.id.image);
            divide = itemView.findViewById(R.id.divide);
        }
    }

    public void setOnItemChildClickListener(OnItemChildClickListener listener){
        this.listener = listener;
    }


    public interface OnItemChildClickListener{
        void onItemChildClick(int position);

        void OnItemClickListener(int position);
    }

}
