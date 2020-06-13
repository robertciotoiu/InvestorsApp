package com.example.robi.budgetize.ui.activities.createActivities;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.robi.budgetize.ApplicationObj;
import com.example.robi.budgetize.R;
import com.example.robi.budgetize.backend.viewmodels.MainActivityViewModel;
import com.example.robi.budgetize.backend.viewmodels.factories.MainActivityViewModelFactory;
import com.example.robi.budgetize.data.localdatabase.entities.CategoryObject;
import com.example.robi.budgetize.data.localdatabase.entities.Wallet;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.maltaisn.icondialog.IconDialog;
import com.maltaisn.icondialog.IconDialogSettings;
import com.maltaisn.icondialog.data.Icon;
import com.maltaisn.icondialog.pack.IconPack;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CreateCategoryActivity extends AppCompatActivity implements IconDialog.Callback {
    private static final String ICON_DIALOG_TAG = "icon-dialog";
    long walletID = 0;
    Wallet wallet;
    MainActivityViewModel mainActivityViewModel;
    private int selectedIconID = 0;
    private Observer<List<Wallet>> walletListObsever;
    IconPack iconPack;

    TextInputLayout iconPicker;
    TextInputLayout categoryNameTextInput;
    TextInputLayout categoryDescriptionTextInput;
    TextInputEditText categoryName;
    TextInputEditText categoryDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_category);
        mainActivityViewModel = new ViewModelProvider(this
                , new MainActivityViewModelFactory((ApplicationObj) this.getApplication()))
                .get(MainActivityViewModel.class);
        Bundle bundle = getIntent().getExtras();
        if (bundle.get("wallet") != null) {
            Gson gson = new Gson();
            String walletAsString = (String) bundle.get("wallet");
            this.wallet = gson.fromJson(walletAsString, Wallet.class);
            walletID = wallet.getId();
            iconPack = getIconDialogIconPack();
        }
        init_listeners();
        init_ui_elements();
        init_icon_picker();
        init_auto_icon_suggest();
    }

    private void init_ui_elements() {
        categoryNameTextInput = this.findViewById(R.id.category_name);
        categoryDescriptionTextInput = this.findViewById(R.id.category_description);

        categoryName = this.findViewById(R.id.name_input_text);
        categoryDescription = this.findViewById(R.id.description_input_text);
    }

    private void init_auto_icon_suggest() {
        categoryName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    Log.d("focus", "focus loosed");
                    autoSuggestIcon();
                    // Do whatever you want here
                } else {
                    Log.d("focus", "focused");
                }
            }
        });
    }

    private void autoSuggestIcon() {
        Drawable backupDrawable = null;
        int backupIconID = 0;

        List<Icon> icons = iconPack != null ? iconPack.getAllIcons() : null;
        for (Icon icon : icons) {
            List<String> iconTags = icon.getTags();
            for (String tag : iconTags) {
                if (categoryName.getText().toString().toUpperCase().contains(tag.toUpperCase())) {
                    if (tag.contentEquals(iconTags.get(0))) {
                        Drawable iconColored = icon.getDrawable();
                        iconColored.setColorFilter(getColor(R.color.positiveBackgroundColor), PorterDuff.Mode.SRC_ATOP);
                        iconPicker.setStartIconDrawable(iconColored);
                        selectedIconID = icon.getId();
                        return;
                    } else if (backupDrawable == null) {
                        backupDrawable = icon.getDrawable();
                        backupIconID = icon.getId();
                    }
                }
            }
        }
        if (backupDrawable != null) {
            backupDrawable.setColorFilter(getColor(R.color.positiveBackgroundColor), PorterDuff.Mode.SRC_ATOP);
            iconPicker.setStartIconDrawable(backupDrawable);
            selectedIconID = backupIconID;
        }
    }

    private void init_icon_picker() {
        iconPicker = findViewById(R.id.icon_picker);
        iconPicker.setStartIconDrawable(iconPack.getIcon(121).getDrawable());
        // If dialog is already added to fragment manager, get it. If not, create a new instance.
        IconDialog dialog = (IconDialog) getSupportFragmentManager().findFragmentByTag(ICON_DIALOG_TAG);
        IconDialog iconDialog = dialog != null ? dialog
                : IconDialog.newInstance(new IconDialogSettings.Builder().build());
        TextInputEditText btn = findViewById(R.id.icon_input_text);
        btn.setOnClickListener(v -> {
            // Open icon dialog
            iconDialog.show(getSupportFragmentManager(), ICON_DIALOG_TAG);
        });

    }

    public void init_listeners() {
        Button add_button = (Button) this.findViewById(R.id.add_button_category);

        add_button.setOnClickListener(v -> createCategory());

        //MainOAuthActivity.wallets.add(new Wallet();
    }

    private void createCategory() {
        String categoryNameString = categoryName.getText().toString();
        String categoryDescriptionString = categoryDescription.getText().toString();

        try {
            long currentWalletID = wallet.getId();
            CategoryObject categoryObject = new CategoryObject(categoryNameString, categoryDescriptionString,selectedIconID, currentWalletID, null);

            long status = mainActivityViewModel.addCategory(categoryObject);//MainActivity.myDatabase.categoryDao().addCategory(categoryObject);

            if (mainActivityViewModel.getCategoryByID(categoryObject.getCategory_id()) != null) {//MainActivity.myDatabase.walletDao().getWalletById(status)!=null) {
                Toast.makeText(this, "Category added successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Category failed to be added", Toast.LENGTH_SHORT).show();
            }

            this.finish(); //closes this activity and return to MainOAuthActivity.java
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            Toast.makeText(this, "Invalid data", Toast.LENGTH_SHORT).show();
        }
    }

    @Nullable
    @Override
    public IconPack getIconDialogIconPack() {
        return ((ApplicationObj) getApplication()).getIconPack();
    }

    @Override
    public void onIconDialogIconsSelected(@NonNull IconDialog dialog, @NonNull List<Icon> icons) {
        // Show a toast with the list of selected icon IDs.
        StringBuilder sb = new StringBuilder();
        for (Icon icon : icons) {
            sb.append(icon.getId());
            sb.append(", ");
            iconPicker.setStartIconDrawable(icon.getDrawable());
            selectedIconID = icon.getId();
        }
        sb.delete(sb.length() - 2, sb.length());
        Toast.makeText(this, "Icons selected: " + sb, Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onIconDialogCancelled() {
    }
}
