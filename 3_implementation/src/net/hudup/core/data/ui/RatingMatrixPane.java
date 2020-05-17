/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.DateFormatter;
import javax.swing.text.NumberFormatter;

import net.hudup.core.Constants;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.Rating;
import net.hudup.core.data.RatingMulti;
import net.hudup.core.data.ctx.ContextTemplateSchema;
import net.hudup.core.data.ctx.ui.ContextListTable;
import net.hudup.core.logistic.ui.MovingToolbar;

/**
 * This is the panel to show rating matrix.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class RatingMatrixPane extends JPanel {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * This class represents a selected rating on rating matrix.
	 * @author Loc Nguyen
	 * @version 1.0
	 */
	protected class SelectedRating {
		
		/**
		 * Selected rating.
		 */
		public Rating rating = null;
		
		/**
		 * Selected rating index.
		 */
		public int index = -1;
		
		/**
		 * Default constructor.
		 */
		public SelectedRating() {
			
		}
		
		/**
		 * Constructor with selected rating and index.
		 * @param rating selected rating.
		 * @param index selected index.
		 */
		public SelectedRating(Rating rating, int index) {
			this.rating = rating;
			this.index = index;
		}
		
		/**
		 * Checking whether this selected rating is valid.
		 * @return whether this selected rating is valid.
		 */
		public boolean isValid() {
			if (rating == null || index < 0)
				return false;
			else if (rating instanceof RatingMulti)
				return index < ((RatingMulti)rating).size();
			else
				return index == 0;
		}
		
	}
	
	
	/**
	 * Internal dataset.
	 */
	protected Dataset dataset = null;
	
	
	/**
	 * Selected rating.
	 */
	protected SelectedRating selectedRating = null;
	
	
	/**
	 * Rating value table.
	 */
	protected RatingValueTable tblRatingValue = null;
	
	
	/**
	 * Rating panel.
	 */
	protected RatingPane paneRating = null;
	
	
	/**
	 * Moving tool bar.
	 */
	protected MovingToolbar tbMove = null;
	
	
	/**
	 * Constructor with specified dataset.
	 * @param dataset specified dataset.
	 */
	public RatingMatrixPane(Dataset dataset) {
		super();
		this.dataset = dataset;
		
		setLayout(new BorderLayout());
		
		JPanel body = new JPanel(new BorderLayout());
		add(body, BorderLayout.CENTER);
		tblRatingValue = new RatingValueTable();
		tblRatingValue.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				viewCell();
			}
			
		});
		
		tblRatingValue.update(dataset, null);
		body.add(new JScrollPane(tblRatingValue), BorderLayout.CENTER);
		
		JPanel footer = new JPanel(new BorderLayout());
		add(footer, BorderLayout.SOUTH);
		paneRating = new RatingPane(dataset.getCTSchema());
		footer.add(paneRating, BorderLayout.CENTER);
		
		tbMove = new MovingToolbar() {
			
			/**
			 * Serial version UID for serializable class. 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void update() {
				if (selectedRating == null || !selectedRating.isValid() || !(selectedRating.rating instanceof RatingMulti) ) {
					setVisible(false);
					return;
				}
				
				RatingMulti mrating = (RatingMulti)selectedRating.rating;
				if (mrating.size() <= 1) {
					setVisible(false);
					return;
				}
				
				setVisible(true);
				enableButtons(false);
				info.setText("" + (selectedRating.index+1) + "/" + mrating.size());
				paneRating.set(mrating.get(selectedRating.index));
				
				if (selectedRating.index == 0) {
					next.setEnabled(true);
					last.setEnabled(true);
				}
				else if (selectedRating.index == mrating.size() - 1) {
					first.setEnabled(true);
					previous.setEnabled(true);
				}
				else {
					first.setEnabled(true);
					previous.setEnabled(true);
					next.setEnabled(true);
					last.setEnabled(true);
				}
			}

			@Override
			public void moveFirst() {
				if (selectedRating == null || !selectedRating.isValid() || !(selectedRating.rating instanceof RatingMulti) )
					return;
				RatingMulti mrating = (RatingMulti)selectedRating.rating;
				if (mrating.size() <= 1) return;

				if (selectedRating.index > 0) {
					selectedRating.index = 0;
					update();
				}
			}

			@Override
			public void movePrevious() {
				if (selectedRating == null || !selectedRating.isValid() || !(selectedRating.rating instanceof RatingMulti) )
					return;
				RatingMulti mrating = (RatingMulti)selectedRating.rating;
				if (mrating.size() <= 1) return;

				if (selectedRating.index > 0) {
					selectedRating.index --;
					update();
				}
			}

			@Override
			public void moveNext() {
				if (selectedRating == null || !selectedRating.isValid() || !(selectedRating.rating instanceof RatingMulti) )
					return;
				RatingMulti mrating = (RatingMulti)selectedRating.rating;
				if (mrating.size() <= 1) return;

				if (selectedRating.index < mrating.size() - 1) {
					selectedRating.index ++;
					update();
				}
			}

			@Override
			public void moveLast() {
				if (selectedRating == null || !selectedRating.isValid() || !(selectedRating.rating instanceof RatingMulti) )
					return;
				RatingMulti mrating = (RatingMulti)selectedRating.rating;
				if (mrating.size() <= 1) return;

				if (selectedRating.index < mrating.size() - 1) {
					selectedRating.index = mrating.size() - 1;
					update();
				}
			}

		};
		footer.add(tbMove, BorderLayout.SOUTH);
		tbMove.setFloatable(false);
		tbMove.update();
	}
	
	
	/**
	 * Viewing cell.
	 */
	protected void viewCell() {
		selectedRating = null;
		paneRating.clear();
		tbMove.update();
		
		int rowIndex = tblRatingValue.getSelectedRow();
		int colIndex = tblRatingValue.getSelectedColumn();
		if (rowIndex == -1 || colIndex == -1)
			return;
		
		RatingValueTM model = tblRatingValue.getRatingValueTM();
		
		int rowIdFound = -1;
		Set<Integer> rowIds = model.rowIndexes.keySet();
		for (int rowId : rowIds) {
			if (model.rowIndexes.get(rowId) == rowIndex) {
				rowIdFound = rowId;
				break;
			}
				
		}
		if (rowIdFound == -1)
			return;
		
		int colIdFound = -1;
		Set<Integer> colIds = model.columnIndexes.keySet();
		for (int colId : colIds) {
			if (model.columnIndexes.get(colId) == colIndex) {
				colIdFound = colId;
				break;
			}
				
		}
		if (colIdFound == -1)
			return;

		Rating rating = dataset.getRating(rowIdFound, colIdFound);
		if (rating == null)
			return;
		else if (rating instanceof RatingMulti) {
			RatingMulti mrating = (RatingMulti)rating;
			if (mrating.size() == 0)
				selectedRating = null;
			else
				selectedRating = new SelectedRating(mrating, mrating.size() -  1);
		}
		else
			selectedRating = new SelectedRating(rating, 0);
		
		paneRating.set(rating);
		tbMove.update();
	}
	
	
	/**
	 * Clearing panel.
	 */
	public void clear() {
		paneRating.clear();
		tblRatingValue.clear();
	}
	
	
}



/**
 * This class represents the rating panel.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
class RatingPane extends JPanel {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Editable flag.
	 */
	boolean editable = false;
	
	/**
	 * Rating.
	 */
	Rating rating = null;
	
	/**
	 * Value text field.
	 */
	JFormattedTextField txtValue = null;
	
	/**
	 * Date text field.
	 */
	JFormattedTextField txtDate = null;
	
	/**
	 * Table of context list.
	 */
	ContextListTable tblContextList = null;
	
	/**
	 * Tool bar.
	 */
	JPanel toolbar = null;
	
	
	/**
	 * Constructor with specified context template schema.
	 * @param cts specified context template schema.
	 */
	public RatingPane(ContextTemplateSchema cts) {
		super(new BorderLayout());
		
		JPanel header = new JPanel(new BorderLayout());
		add(header, BorderLayout.NORTH);
		
		JPanel left = new JPanel(new GridLayout(0, 1));
		header.add(left, BorderLayout.WEST);
		left.add(new JLabel("Rated value:"));
		left.add(new JLabel("Rated date:"));
		
		JPanel center = new JPanel(new GridLayout(0, 1));
		header.add(center, BorderLayout.CENTER);
		//
		txtValue = new JFormattedTextField(new NumberFormatter());
		center.add(txtValue);
		//
		txtDate = new JFormattedTextField(
				new DateFormatter(new SimpleDateFormat(Constants.DATE_FORMAT)));
		center.add(txtDate);
		
		JPanel body = new JPanel(new BorderLayout());
		add(body, BorderLayout.CENTER);
		tblContextList = new ContextListTable(cts);
		tblContextList.setEnabled(editable);
		tblContextList.setPreferredScrollableViewportSize(new Dimension(200, 50));
		body.add(new JScrollPane(tblContextList), BorderLayout.CENTER);
		
		toolbar = new JPanel();
		add(toolbar, BorderLayout.SOUTH);
		
		JButton apply = new JButton("Apply");
		toolbar.add(apply);
		apply.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean apply = apply();
				if (apply) {
					JOptionPane.showMessageDialog(
							getThis(), 
							"Apply successfully", "Apply successfully", JOptionPane.INFORMATION_MESSAGE);
				}
				else {
					JOptionPane.showMessageDialog(
						getThis(), 
						"Fail to apply", "Fail to apply", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		
		JButton reset = new JButton("Reset");
		toolbar.add(reset);
		reset.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				set(rating);
			}
		});
		
		
		setEditable(false);
	}
	
	
	/**
	 * Getting this panel.
	 * @return this rating matrix pane.
	 */
	private RatingPane getThis() {
		return this;
	}
	
	
	/**
	 * Setting rating panel.
	 * @param rating the specified rating.
	 */
	public void set(Rating rating) {
		clear();
		
		if (rating != null) {
			this.rating = rating;
			txtValue.setValue(rating.value);
			txtDate.setValue(new Date(rating.ratedDate));
			tblContextList.set(rating.contexts);
		}
	}
	
	
	/**
	 * Applying value.
	 * @return whether apply successfully.
	 */
	protected boolean apply() {
		if (!validateValues()) {
			JOptionPane.showMessageDialog(this, "Invalid enter data", "Invalid enter data", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		if (rating != null) {
			rating.value = ((Number)txtValue.getValue()).doubleValue();
			rating.ratedDate = txtDate.getValue() != null ? ((Date)txtDate.getValue()).getTime() : 0;
			return true & tblContextList.apply();
		}
		else
			return false;
	}
	
	
	/**
	 * Validating values.
	 * @return whether values are valid.
	 */
	protected boolean validateValues() {
		Object ratedValue = txtValue.getValue();
		if (ratedValue == null || !(ratedValue instanceof Number))
			return false;
		
		Object ratedDate = txtDate.getValue();
		if (ratedDate == null || !(ratedDate instanceof Date))
			return false;
		
		return true;
	}
	
	
	/**
	 * Clearing values.
	 */
	public void clear() {
		txtValue.setValue(null);
		txtDate.setValue(null);
		tblContextList.clear();
	}
	
	
	/**
	 * Checking whether rating is editable.
	 * @return whether rating is editable
	 */
	public boolean isEditable() {
		return editable;
	}
	
	
	/**
	 * Setting whether rating is editable.
	 * @param editable editable flag.
	 */
	public void setEditable(boolean editable) {
		this.editable = editable;
		
		txtValue.setEditable(editable);
		txtDate.setEditable(editable);
		tblContextList.setEnabled(editable);
		toolbar.setVisible(editable);
	}
	
	
}
