package com.example.android.autonomistock;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amazonaws.models.nosql.CartDO;
import com.amazonaws.models.nosql.IssuesDO;

import java.util.ArrayList;

/**
 * Created by kartheek on 21/4/18.
 */

public class IssuesAdapter extends RecyclerView.Adapter<IssuesAdapter.MyIssueViewHolder> {

    private Context mContext;
    private ArrayList<IssuesDO> issues;

    public IssuesAdapter(Context mContext, ArrayList<IssuesDO> issues) {
        this.mContext = mContext;
        this.issues = issues;
    }

    @Override
    public IssuesAdapter.MyIssueViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.issues_row, parent, false);
        return new MyIssueViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(IssuesAdapter.MyIssueViewHolder holder, int position) {
        holder.bind(issues.get(position));
    }

    @Override
    public int getItemCount() {
        return issues.size();
    }

    public class MyIssueViewHolder extends RecyclerView.ViewHolder {

        public TextView issueId;
        public TextView description;

        public MyIssueViewHolder(View view) {
            super(view);
            issueId = (TextView) view.findViewById(R.id.issueId);
            description = (TextView) view.findViewById(R.id.description);
        }

        public void bind(final IssuesDO item) {
            issueId.setText("Issue ID - #" + (int) item.getIssueId() + "");
            description.setText(item.getDescription());
        }
    }

}
