package edu.iastate.metnet.metaomgraph.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ColorUIResource;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import org.jfree.chart.axis.ValueAxis;

import edu.iastate.metnet.metaomgraph.AnimatedSwingWorker;
import edu.iastate.metnet.metaomgraph.FrameModel;
import edu.iastate.metnet.metaomgraph.MetaOmAnalyzer;
import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.MetadataCollection;
import edu.iastate.metnet.metaomgraph.MetadataHybrid;
import edu.iastate.metnet.metaomgraph.chart.MetaOmChartPanel;
import edu.iastate.metnet.metaomgraph.logging.ActionProperties;
import edu.iastate.metnet.metaomgraph.Metadata.MetadataQuery;

import javax.swing.JMenuBar;
import javax.swing.JOptionPane;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.JMenu;
import javax.swing.JSplitPane;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;

import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyVetoException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.awt.event.ActionEvent;

public class MetadataFilter extends TaskbarInternalFrame {

	private JPanel contentPane;
	private JTable table;
	private JTable table_1;
	private MetadataCollection mogColl;
	private Set<String> included;
	private Set<String> excluded;
	private HashMap<Integer, List<String>> toHighlight_inc;
	private HashMap<Integer, List<String>> toHighlight_exc;
	private List<String> searchIncres;
	private List<String> searchExcres;
	private boolean delete;
	private MetaOmChartPanel chartPanel;
	/**
	 * Default Properties
	 */
	//urmi disable black selection background
	//private Color SELECTIONBCKGRND = Color.black;
	private Color BCKGRNDCOLOR1 = Color.white;
	private Color BCKGRNDCOLOR2 = new ColorUIResource(216, 236, 213);
	private Color HIGHLIGHTCOLOR = Color.ORANGE;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					MetadataFilter frame = new MetadataFilter(null);
					FrameModel fm = new FrameModel("Metadata Filter","Metadata Filter",31);
					//					frame.setModel(fm);
					MetaOmGraph.getDesktop().add(frame);
					frame.setVisible(true);
					frame.show();
					//					frame.moveToFront();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	// public MetadataFilter() {
	// this();

	// }

	/**
	 * Create the frame.
	 */
	public MetadataFilter() {
		this(null);

	}

	public MetadataFilter(MetadataCollection ob) {
		this(ob, false);

	}

	public MetadataFilter(MetadataCollection ob, boolean val) {
		this(ob,val,null);
	}

	public MetadataFilter(MetadataCollection metadataCollection, boolean val, MetaOmChartPanel thisChartPanel) {
		super("Metadata Filter");


		// TODO Auto-generated constructor stub
		this.delete = val;
		this.chartPanel=thisChartPanel;
		//setModal(true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		this.mogColl = metadataCollection;
		included = mogColl.getIncluded();
		excluded = mogColl.getExcluded();
		// JOptionPane.showMessageDialog(null, "inc:"+included.toString());
		// JOptionPane.showMessageDialog(null, "exc:"+excluded.toString());
		JMenuBar menuBar = new JMenuBar();
		//setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.NORTH);
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JButton btnDone;
		if (delete) {
			btnDone = new JButton("Delete");
		} else {
			btnDone = new JButton("Filter");
		}
		btnDone.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				/*if(getTablerows(table_1).size()<1) {
						JOptionPane.showMessageDialog(null, "Please use buttons to move selected columns to the excluded list", "Excluded list empty", JOptionPane.ERROR_MESSAGE);
						return;
					}*/

				if (delete) {
					int result = JOptionPane.showConfirmDialog((Component) null, "This will delete the rows in the excluded list. This can't be undone","Delete rows", JOptionPane.OK_CANCEL_OPTION);
					if(result==JOptionPane.CANCEL_OPTION) {
						return;
					}
					updateIncludedlist();
					//add rows to removedMD list
					Set<String> rowsDeleted = new HashSet<String>(mogColl.getExcluded());

					MetaOmGraph.getActiveProject().getMetadataHybrid().addExcludedMDRows(mogColl.getExcluded());
					//add these to list of missing as these will be deleted from the project
					MetaOmGraph.getActiveProject().getMetadataHybrid().addMissingMDRows(mogColl.getExcluded());
					removeExcludedRows();

					((DefaultTableModel) table_1.getModel()).setRowCount(0);

					//Harsha - reproducibility log
					HashMap<String,Object> actionMap = new HashMap<String,Object>();
					actionMap.put("parent",MetaOmGraph.getCurrentProjectActionId());
					actionMap.put("section", "Sample Metadata Table");

					HashMap<String,Object> dataMap = new HashMap<String,Object>();
					dataMap.put("Deleted Rows",rowsDeleted);

					HashMap<String,Object> resultLog = new HashMap<String,Object>();
					resultLog.put("result", "OK");

					ActionProperties filterSelectedRowsAction = new ActionProperties("delete-metadata-rows",actionMap,dataMap,resultLog,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz").format(new Date()));
					filterSelectedRowsAction.logActionProperties();



				} else {
					updateIncludedlist();
					// update exclude list
					// JOptionPane.showMessageDialog(null, "exlist"+excluded.toString());
					MetaOmAnalyzer.updateExcluded(metadataCollection.getExcluded(), true);
				}
				MetaOmGraph.getActiveTable().updateMetadataTable();
				
				if(chartPanel!=null) {
					chartPanel.updateChartAfterFilter();
				}
			}
		});
		panel.add(btnDone);

		JSeparator separator = new JSeparator();
		separator.setOrientation(SwingConstants.VERTICAL);
		panel.add(separator);

		JButton btnSwapIncludedAnd = new JButton("Swap included and excluded");
		btnSwapIncludedAnd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//switch included and excluced lists
				//s;
				DefaultTableModel model_inc = (DefaultTableModel) table.getModel();
				DefaultTableModel model_exc = (DefaultTableModel) table_1.getModel();
				table.setModel(model_exc);
				table_1.setModel(model_inc);

			}
		});
		panel.add(btnSwapIncludedAnd);

		JSplitPane splitPane = new JSplitPane();
		contentPane.add(splitPane, BorderLayout.CENTER);
		splitPane.setDividerSize(2);
		splitPane.setResizeWeight(.50d);

		JPanel panel_1 = new JPanel();
		splitPane.setLeftComponent(panel_1);
		panel_1.setLayout(new BorderLayout(0, 0));

		JPanel panel_2 = new JPanel();
		panel_1.add(panel_2, BorderLayout.NORTH);
		//panel_2.setLayout(new BoxLayout(panel_2, BoxLayout.LINE_AXIS));
		panel_2.setLayout(new FlowLayout());

		JButton btnSearch = new JButton("Search Included");
		btnSearch.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				/*
				 * try { SimpleSearchDialog dialog = new SimpleSearchDialog(); searchIncres =
				 * dialog.showDialog(); if (searchIncres == null || searchIncres.size() == 0) {
				 * JOptionPane.showMessageDialog(null, "Nothing found", "Search results",
				 * JOptionPane.WARNING_MESSAGE); } setSelected(searchIncres, table); //
				 * table.repaint(); } catch (Exception ex) { ex.printStackTrace(); }
				 */

				// show advance search dialog
				final TreeSearchQueryConstructionPanel tsp = new TreeSearchQueryConstructionPanel(
						MetaOmGraph.getActiveProject(),false);
				final MetadataQuery[] queries;
				queries = tsp.showSearchDialog();
				if (tsp.getQueryCount() <= 0) {
					// System.out.println("Search dialog cancelled");
					// User didn't enter any queries
					return;
				}
				// final int[] result = new
				// int[MetaOmGraph.getActiveProject().getDataColumnCount()];
				final List<String> result = new ArrayList<>();

				new AnimatedSwingWorker("Searching...", true) {

					@Override
					public Object construct() {

						List<String> hits = MetaOmGraph.getActiveProject().getMetadataHybrid().getMatchingRows(queries,	tsp.matchAll());

						// return if no hits
						if (hits.size() == 0) {
							// JOptionPane.showMessageDialog(null, "hits len:"+hits.length);
							// nohits=true;
							result.add("NULL");
							return null;
						} else {
							for (int i = 0; i < hits.size(); i++) {
								result.add(hits.get(i));
							}
						}

						return null;
					}

				}.start();

				searchIncres = result;
				if (searchIncres == null || searchIncres.size() == 0) {
					JOptionPane.showMessageDialog(null, "Nothing found", "Search results", JOptionPane.WARNING_MESSAGE);
				}
				//bring matched item to top and select them
				setSelected(searchIncres, table);

			}
		});
		panel_2.add(btnSearch);

		JButton btnMoveSelected = new JButton("Move selected");
		btnMoveSelected.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				/**
				 * Transfer selected rows of included to excluded
				 * 
				 */
				DefaultTableModel model_inc = (DefaultTableModel) table.getModel();
				DefaultTableModel model_exc = (DefaultTableModel) table_1.getModel();
				int[] selected = table.getSelectedRows();
				// JOptionPane.showMessageDialog(null, "s:"+Arrays.toString(selected));
				String[] temp = new String[1];
				for (int c = 0; c < selected.length; c++) {
					temp[0] = model_inc.getValueAt(table.convertRowIndexToModel(selected[c]), 0).toString();
					model_exc.addRow(temp);
				}
				// remove rows from included
				Arrays.sort(selected);
				// JOptionPane.showMessageDialog(null, "s:"+Arrays.toString(selected));
				for (int i = selected.length - 1; i >= 0; i--) {
					// JOptionPane.showMessageDialog(null, "si:"+selected[i]);
					model_inc.removeRow(table.convertRowIndexToModel(selected[i]));
					// model_inc.fireTableRowsDeleted(selected[i], selected[i]);
				}

			}
		});
		panel_2.add(btnMoveSelected);

		JButton btnMoveAll = new JButton("Move all");
		btnMoveAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				/**
				 * Transfer all rows of included to excluded
				 * 
				 */
				DefaultTableModel model_inc = (DefaultTableModel) table.getModel();
				DefaultTableModel model_exc = (DefaultTableModel) table_1.getModel();
				String[] temp = new String[1];
				for (int c = 0; c < model_inc.getRowCount(); c++) {

					temp[0] = model_inc.getValueAt(c, 0).toString();
					model_exc.addRow(temp);
				}
				model_inc.setRowCount(0);

			}
		});
		panel_2.add(btnMoveAll);

		JScrollPane scrollPane = new JScrollPane();
		panel_1.add(scrollPane, BorderLayout.CENTER);

		// table = new JTable();
		initTables();
		scrollPane.setViewportView(table);

		JPanel panel_3 = new JPanel();
		splitPane.setRightComponent(panel_3);
		panel_3.setLayout(new BorderLayout(0, 0));

		JPanel panel_4 = new JPanel();
		panel_3.add(panel_4, BorderLayout.NORTH);

		//panel_4.setLayout(new BoxLayout(panel_4, BoxLayout.LINE_AXIS));
		panel_4.setLayout(new FlowLayout());
		JButton btnMoveAll_1 = new JButton("Move all");
		btnMoveAll_1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				/**
				 * Transfer all rows of excluded to included
				 * 
				 */
				DefaultTableModel model_inc = (DefaultTableModel) table.getModel();
				DefaultTableModel model_exc = (DefaultTableModel) table_1.getModel();
				String[] temp = new String[1];
				for (int c = 0; c < model_exc.getRowCount(); c++) {

					temp[0] = model_exc.getValueAt(c, 0).toString();
					model_inc.addRow(temp);
				}
				model_exc.setRowCount(0);
			}
		});
		panel_4.add(btnMoveAll_1);

		JButton btnMoveSelected_1 = new JButton("Move selected");
		btnMoveSelected_1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				/**
				 * Transfer selected rows of included to excluded
				 * 
				 */
				DefaultTableModel model_inc = (DefaultTableModel) table.getModel();
				DefaultTableModel model_exc = (DefaultTableModel) table_1.getModel();
				int[] selected = table_1.getSelectedRows();
				String[] temp = new String[1];
				for (int c = 0; c < selected.length; c++) {
					temp[0] = model_exc.getValueAt(table_1.convertRowIndexToModel(selected[c]), 0).toString();
					model_inc.addRow(temp);

				}
				// remove rows from excluded
				Arrays.sort(selected);
				for (int i = selected.length - 1; i >= 0; i--) {
					// JOptionPane.showMessageDialog(null, "si:"+selected[i]);
					model_exc.removeRow(table_1.convertRowIndexToModel(selected[i]));
					// model_inc.fireTableRowsDeleted(selected[i], selected[i]);
				}
			}
		});
		panel_4.add(btnMoveSelected_1);

		JButton btnSearch_1 = new JButton("Search excluded");
		btnSearch_1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				/*
				 * try { SimpleSearchDialog dialog = new SimpleSearchDialog(); searchExcres =
				 * dialog.showDialog(); if (searchExcres == null || searchExcres.size() == 0) {
				 * JOptionPane.showMessageDialog(null, "Nothing found", "Search results",
				 * JOptionPane.WARNING_MESSAGE); } setSelected(searchExcres, table_1); //
				 * table.repaint(); } catch (Exception ex) { ex.printStackTrace(); }
				 */

				// show advance search dialog

				final TreeSearchQueryConstructionPanel tsp = new TreeSearchQueryConstructionPanel(
						MetaOmGraph.getActiveProject());
				final MetadataQuery[] queries;
				queries = tsp.showSearchDialog();
				if (tsp.getQueryCount() <= 0) {
					// System.out.println("Search dialog cancelled");
					// User didn't enter any queries
					return;
				}
				// final int[] result = new
				// int[MetaOmGraph.getActiveProject().getDataColumnCount()];
				final List<String> result = new ArrayList<>();

				new AnimatedSwingWorker("Searching...", true) {

					@Override
					public Object construct() {

						List<String> hits = MetaOmGraph.getActiveProject().getMetadataHybrid().getMatchingRows(queries,
								tsp.matchAll());

						// return if no hits
						if (hits.size() == 0) {
							// JOptionPane.showMessageDialog(null, "hits len:"+hits.length);
							// nohits=true;
							result.add("NULL");
							return null;
						} else {
							for (int i = 0; i < hits.size(); i++) {
								result.add(hits.get(i));
							}
						}

						return null;
					}

				}.start();

				searchExcres = result;
				//JOptionPane.showMessageDialog(null, "res:" + searchExcres.toString());
				if (searchExcres == null || searchExcres.size() == 0) {
					JOptionPane.showMessageDialog(null, "Nothing found", "Search results", JOptionPane.WARNING_MESSAGE);
				}
				setSelected(searchExcres, table_1);
			}
		});
		panel_4.add(btnSearch_1);

		JScrollPane scrollPane_1 = new JScrollPane();
		panel_3.add(scrollPane_1, BorderLayout.CENTER);

		scrollPane_1.setViewportView(table_1);
		//this.setSize(btnMoveSelected_1.getWidth()*8, 700);
		this.setTitle("Advance Sample Filter");
		//this.setMaximumSize(new Dimension(2000, 700));
		this.pack();

		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Rectangle bounds = env.getMaximumWindowBounds();

		int width = Math.min(this.getWidth(), bounds.width);
		int height = bounds.height-200;
		this.setSize( new Dimension(width, height) );

		MetadataFilter thisFrame = this;
		this.addComponentListener(new ComponentListener() {

			@Override
			public void componentShown(ComponentEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void componentResized(ComponentEvent e) {
				// TODO Auto-generated method stub

				if(thisFrame.getWidth() < width || thisFrame.getHeight() < height) {
					thisFrame.setSize(width, height);
				}
			}

			@Override
			public void componentMoved(ComponentEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void componentHidden(ComponentEvent e) {
				// TODO Auto-generated method stub

			}
		});

		this.setClosable(true);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setResizable(true);
		this.setIconifiable(true);
		this.setMaximizable(false);



	}

	private void initTables() {

		toHighlight_inc = new HashMap<Integer, List<String>>();
		toHighlight_exc = new HashMap<Integer, List<String>>();
		// initialize with garbage value for alternate coloring to take effect via
		// prepareRenderer
		toHighlight_inc.put(0, null);
		toHighlight_exc.put(0, null);
		// List<String> ls = new ArrayList<String>();
		// ls.add("Exp1");
		// toHighlight.put(0, ls);
		// headers = obj.getHeaders();
		table = new JTable() {
			@Override
			public boolean getScrollableTracksViewportWidth() {
				return getPreferredSize().width < getParent().getWidth();
			}

			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				Component c = super.prepareRenderer(renderer, row, column);

				if (!isRowSelected(row)) {
					c.setBackground(getBackground());
					int modelRow = convertRowIndexToModel(row);

					// for (int j = 0; j < table.getColumnCount(); j++) {
					for (Integer j : toHighlight_inc.keySet()) {
						String type = (String) getModel().getValueAt(modelRow, j);
						if (highlightThisRow(toHighlight_inc, j, type)) {
							c.setBackground(HIGHLIGHTCOLOR);
						} else {
							if (row % 2 == 0) {
								c.setBackground(BCKGRNDCOLOR1);
							} else {
								c.setBackground(BCKGRNDCOLOR2);
							}
						}
					}
					/*
					 * String type = (String) getModel().getValueAt(modelRow, 0); if
					 * ("Exp1".equals(type)) c.setBackground(Color.GREEN); if ("Sell".equals(type))
					 * c.setBackground(Color.YELLOW);
					 */
				} else {
					//c.setBackground(SELECTIONBCKGRND);
				}

				return c;
			}
		};

		table.setModel(new DefaultTableModel() {

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		});

		DefaultTableModel tablemodel = (DefaultTableModel) table.getModel();
		// add data
		// for each row add each coloumn
		String[] temp = new String[1];
		// temp[0] = "Data Column";
		// tablemodel.addRow(temp);
		tablemodel.addColumn(mogColl.getDatacol() + "(included)");

		List<String> tempListinc=new ArrayList<>(included);

		for (int i = 0; i < tempListinc.size(); i++) {
			// JOptionPane.showMessageDialog(null,included.get(i).toString() );
			temp[0] = tempListinc.get(i).toString();
			tablemodel.addRow(temp);
		}

		// table.setBackground(Color.GREEN);
		table.setAutoCreateRowSorter(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setPreferredScrollableViewportSize(table.getPreferredSize());
		table.setFillsViewportHeight(true);
		table.getTableHeader().setFont(new Font("Garamond", Font.BOLD, 14));

		////////////////////// init exclude table//////////////////////
		table_1 = new JTable() {
			@Override
			public boolean getScrollableTracksViewportWidth() {
				return getPreferredSize().width < getParent().getWidth();
			}

			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				Component c = super.prepareRenderer(renderer, row, column);

				if (!isRowSelected(row)) {
					c.setBackground(getBackground());
					int modelRow = convertRowIndexToModel(row);

					// for (int j = 0; j < table.getColumnCount(); j++) {
					for (Integer j : toHighlight_exc.keySet()) {
						String type = (String) getModel().getValueAt(modelRow, j);
						if (highlightThisRow(toHighlight_exc, j, type)) {
							c.setBackground(HIGHLIGHTCOLOR);
						} else {
							if (row % 2 == 0) {
								c.setBackground(BCKGRNDCOLOR1);
							} else {
								c.setBackground(BCKGRNDCOLOR2);
							}
						}
					}
					/*
					 * String type = (String) getModel().getValueAt(modelRow, 0); if
					 * ("Exp1".equals(type)) c.setBackground(Color.GREEN); if ("Sell".equals(type))
					 * c.setBackground(Color.YELLOW);
					 */
				} else {
					//c.setBackground(SELECTIONBCKGRND);
				}

				return c;
			}
		};

		table_1.setModel(new DefaultTableModel() {

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		});

		DefaultTableModel tablemodel_1 = (DefaultTableModel) table_1.getModel();
		// add data
		tablemodel_1.addColumn(mogColl.getDatacol() + "(excluded)");

		List<String> tempListexc=new ArrayList<>(excluded);
		for (int i = 0; i < tempListexc.size(); i++) {
			// JOptionPane.showMessageDialog(null,excluded.get(i).toString() );
			temp[0] = tempListexc.get(i).toString();
			tablemodel_1.addRow(temp);
		}

		// table.setBackground(Color.GREEN);
		table_1.setAutoCreateRowSorter(true);
		table_1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table_1.setPreferredScrollableViewportSize(table.getPreferredSize());
		table_1.setFillsViewportHeight(true);
		table_1.getTableHeader().setFont(new Font("Garamond", Font.BOLD, 14));

	}

	private boolean highlightThisRow(HashMap<Integer, List<String>> toHighlight, int col, String val) {
		boolean result = false;
		if (toHighlight == null || toHighlight.size() < 1) {
			return false;
		}
		// search
		List<String> allVals = toHighlight.get(col);
		if (allVals == null) {
			return false;
		}
		if (allVals.contains(val)) {
			return true;
		}
		return result;
	}


	/**
	 * @author urmi
	 * bring the matched items to top and set them as selected
	 * @param res
	 * @param tab
	 */
	private void setSelected(List<String> res, JTable tab) {
		DefaultTableModel model = (DefaultTableModel) tab.getModel();
		List<String> newVals = new ArrayList<>();
		// bring matched values at top
		String temp;
		int total_matches = 0;
		for (int c = 0; c < model.getRowCount(); c++) {
			temp = model.getValueAt(c, 0).toString();
			if (res.contains(temp)) {
				newVals.add("\t:::" + temp);
				total_matches++;
			} else {
				newVals.add("~:::" + temp);
			}
		}

		java.util.Collections.sort(newVals);
		// JOptionPane.showMessageDialog(null, newVals.toString());
		model.setRowCount(0);

		for (int i = 0; i < newVals.size(); i++) {
			Vector<String> v = new Vector<>();
			v.add(newVals.get(i).split(":::")[1]);
			model.addRow(v);

		}
		if (total_matches > 0) {
			tab.setRowSelectionInterval(0, total_matches - 1);
		}
	}

	public void updateIncludedlist() {
		mogColl.setIncluded(getTablerows(table));
		mogColl.setExcluded(getTablerows(table_1));

		HashMap<String,Object> actionMap = new HashMap<String,Object>();
		HashMap<String,Object> dataMap = new HashMap<String,Object>();
		HashMap<String,Object> result = new HashMap<String,Object>();

	}

	public void removeExcludedRows() {
		mogColl.removeDataPermanently();
	}

	List<String> getTablerows(JTable tab) {
		List<String> res = new ArrayList<>();
		DefaultTableModel model = (DefaultTableModel) tab.getModel();
		for (int c = 0; c < model.getRowCount(); c++) {
			res.add(model.getValueAt(c, 0).toString());
		}
		return res;
	}

	Map<Integer,String> getTableRowInfo(JTable tab) {
		Map<Integer,String> res = new HashMap<Integer,String>();
		DefaultTableModel model = (DefaultTableModel) tab.getModel();
		for (int c = 0; c < model.getRowCount(); c++) {
			res.put(c,model.getValueAt(c, 0).toString());
		}
		return res;
	}


	public void setDelete(boolean val) {
		this.delete = val;
	}
}
