package com.moogu.myweb.client.feature.management.usernames;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HTML;
import com.moogu.myweb.client.MyWebEntryPoint;
import com.moogu.myweb.client.RemoteServicePool;
import com.moogu.myweb.client.common.IlmsAsyncCallback;
import com.moogu.myweb.client.common.widget.Button;
import com.moogu.myweb.client.common.widget.dialog.PromptDialog;
import com.moogu.myweb.client.common.widget.misc.Separator;
import com.moogu.myweb.shared.common.SUser;

/**
 * Manage the user name: possibility to rename it
 * 
 * @author i20075
 */
public class UserNamesPanel extends LayoutContainer {

	private static final String USER_CODE_PROP = "code";

	private static final String USER_NAME_PROP = "name";

	private final ListStore<ModelData> storeUser = new ListStore<ModelData>();

	private ColumnModel cmUser = null;

	private Button updateUser = new Button("Rename");

	private Grid<ModelData> gridUser = null;

	/**
	 * Constructor It contains logic for update button (instantiation)
	 */
	public UserNamesPanel() {
		updateUser = new Button("Rename", new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				/** get user from grid */
				final ModelData userModelData = gridUser.getSelectionModel()
						.getSelectedItem();

				/** check if one user is selected */
				if (userModelData == null) {
					MessageBox.alert("Error",
							"Please select one user before renaming.", null);
					return;
				}

				final PromptDialog dialog = new PromptDialog("User name", 150,
						false, 3, 60, "Rename User Name", "User") {

					@Override
					public void buttonOKClicked(String field) {
						userModelData.set(UserNamesPanel.USER_NAME_PROP, field);
						UserNamesPanel.this.updateUser(userModelData);
					}
				};
				dialog.show();
			}
		});
	}

	private void configUserColumns() {
		final List<ColumnConfig> columnConfigUser = new ArrayList<ColumnConfig>();

		// column code
		ColumnConfig column = new ColumnConfig();
		column.setId(UserNamesPanel.USER_CODE_PROP);
		column.setHeader("User");
		column.setWidth(70);
		columnConfigUser.add(column);

		// name column
		column = new ColumnConfig();
		column.setId(UserNamesPanel.USER_NAME_PROP);
		column.setHeader("Name");
		column.setWidth(180);
		columnConfigUser.add(column);

		/** column model for user */
		cmUser = new ColumnModel(columnConfigUser);
	}

	@Override
	protected void onLoad() {
		RemoteServicePool.getCommonModule().getUsersOrderedByName(
				new IlmsAsyncCallback<List<SUser>>(true) {

					@Override
					public void onCustomSuccess(List<SUser> userListResult) {
						final List<ModelData> modelDataList = UserNamesPanel.this
								.toModelDataList(userListResult);
						storeUser.removeAll();
						storeUser.add(modelDataList);
					}
				});
	}

	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		setLayout(new RowLayout(Orientation.VERTICAL));
		configUserColumns();
		setUserGrid();
		setPanel();
	}

	private void setPanel() {
		updateUser.setSize(70, 22);
		updateUser.setEnabled(MyWebEntryPoint
				.isUserRole(MyWebEntryPoint.ROLE_3));

		final FieldSet userSet = new FieldSet();
		userSet.setLayout(new RowLayout(Orientation.VERTICAL));
		userSet.setStyleAttribute("margin", "5px");
		userSet.setHeading("Edit users");

		final HorizontalPanel panel = new HorizontalPanel();
		panel.add(updateUser);

		panel.add(new Separator(10));
		userSet.add(panel);
		/** jump a line */
		userSet.add(new HTML("&nbsp;"));

		userSet.add(gridUser, new RowData(500, 200));
		this.add(userSet, new RowData(1234, 310));
		this.setHeight(766);
	}

	private void setUserGrid() {
		storeUser.sort(cmUser.getColumnId(0), SortDir.ASC);
		gridUser = new Grid<ModelData>(storeUser, cmUser);
		gridUser.setAutoExpandColumn("name");
		gridUser.setBorders(true);
		gridUser.setSize(480, 200);
		gridUser.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
	}

	private List<ModelData> toModelDataList(List<SUser> users) {
		final List<ModelData> modelDataList = new ArrayList<ModelData>();
		for (final SUser user : users) {
			final ModelData modelData = new BaseModelData();
			modelData.set(UserNamesPanel.USER_CODE_PROP, user.getCode());
			modelData.set(UserNamesPanel.USER_NAME_PROP, user.getName());
			modelDataList.add(modelData);
		}
		return modelDataList;
	}

	private void updateUser(final ModelData userToBeUpdated) {
		final String code = userToBeUpdated.get(UserNamesPanel.USER_CODE_PROP);
		final String newName = userToBeUpdated
				.get(UserNamesPanel.USER_NAME_PROP);
		RemoteServicePool.getCommonModule().renameUser(code, newName,
				new IlmsAsyncCallback<Void>(true) {

					@Override
					public void onCustomSuccess(Void userUpdateResult) {
						storeUser.remove(gridUser.getSelectionModel()
								.getSelectedItem());
						storeUser.add(userToBeUpdated);
					}
				});

	}

}