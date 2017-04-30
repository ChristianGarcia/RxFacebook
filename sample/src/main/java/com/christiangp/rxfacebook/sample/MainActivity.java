/*
 * Copyright (c) 2017 Christian García
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.christiangp.rxfacebook.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;

public class MainActivity
    extends AppCompatActivity {

    private static final List<Pair<String, Class<? extends Activity>>> SECTIONS = Arrays.asList(
        Pair.create("Get Access Token (Sign in)", SignInActivity.class),
        Pair.create("Me Request", MeRequestActivity.class)
    );

    @BindView(R.id.sectionsView)
    RecyclerView sectionsView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        sectionsView.setLayoutManager(new LinearLayoutManager(this));
        sectionsView.setAdapter(new SectionsAdapter(SECTIONS, activityClass -> startActivity(new Intent(this, activityClass))));
    }

    private static class SectionsAdapter
        extends RecyclerView.Adapter<SectionViewHolder> {

        private final List<Pair<String, Class<? extends Activity>>> sections;

        private final Consumer<Class<? extends Activity>>           onSectionClickedListener;

        public SectionsAdapter(
            List<Pair<String, Class<? extends Activity>>> sections,
            Consumer<Class<? extends Activity>> onSectionClickedListener
        ) {
            this.sections = sections;
            this.onSectionClickedListener = onSectionClickedListener;
        }

        @Override
        public SectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new SectionViewHolder(parent, onSectionClickedListener);
        }

        @Override
        public void onBindViewHolder(SectionViewHolder holder, int position) {
            holder.populate(sections.get(position));
        }

        @Override
        public int getItemCount() {
            return sections.size();
        }
    }

    static class SectionViewHolder
        extends RecyclerView.ViewHolder {

        private Class<? extends Activity> activityClass;

        SectionViewHolder(ViewGroup parent, Consumer<Class<? extends Activity>> onSectionClickedListener) {
            super(LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_section, parent, false));
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(__ -> {
                try {
                    onSectionClickedListener.accept(activityClass);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }

        void populate(Pair<String, Class<? extends Activity>> section) {
            ((TextView) itemView).setText(section.first);
            activityClass = section.second;
        }
    }
}
