package view;

import interface_adapter.get_recipe.GetRecipeController;
import interface_adapter.get_recipe.GetRecipeState;
import interface_adapter.get_recipe.GetRecipeViewModel;
import interface_adapter.get_shopping_list.GetShoppingListController;
import interface_adapter.get_shopping_list.GetShoppingListState;
import interface_adapter.get_shopping_list.GetShoppingListViewModel;
import interface_adapter.ViewManagerModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class GetRecipeView extends JPanel implements ActionListener, PropertyChangeListener {
    public final String viewName = "get recipe";
    public final JButton MainMenu;
    private final GetRecipeViewModel getRecipeViewModel;
    private final GetShoppingListViewModel getShoppingListViewModel;
    private final ViewManagerModel viewManagerModel;
    private final GetRecipeController getRecipeController;
    private final GetShoppingListController getShoppingListController;

    private final JButton generate;
    private final JPanel recipesPanel;

    public GetRecipeView(ViewManagerModel viewManagerModel,
                         GetRecipeViewModel getRecipeViewModel,
                         GetRecipeController getRecipeController,
                         GetShoppingListViewModel getShoppingListViewModel,
                         GetShoppingListController getShoppingListController) {
        this.getRecipeViewModel = getRecipeViewModel;
        this.viewManagerModel = viewManagerModel;
        this.getRecipeController = getRecipeController;
        this.getShoppingListViewModel = getShoppingListViewModel;
        this.getShoppingListController = getShoppingListController;
        ShoppingListGenerator.setPath("./output/Shopping_List.md");
        getRecipeViewModel.addPropertyChangeListener(this);
        getShoppingListViewModel.addPropertyChangeListener(this);

        JLabel title = new JLabel(GetRecipeViewModel.TITLE_LABEL);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(title);

        JPanel buttons = new JPanel();

        // Back to Main Menu button
        MainMenu = new JButton(GetRecipeViewModel.MAIN_MENU);
        buttons.add(MainMenu);
        add(buttons);


        generate = new JButton(GetRecipeViewModel.GET_RECIPE_BUTTON_LABEL);
        buttons.add(generate);
        add(buttons);

        recipesPanel = new JPanel();
//        recipesPanel.setPreferredSize(new Dimension(640, 500));
        JScrollPane scrollPane = new JScrollPane(recipesPanel);
        scrollPane.setPreferredSize(new Dimension(640,180));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane);

//        resultTextArea = new JTextArea();
//        resultTextArea.setEditable(false);
//        resultTextArea.setLineWrap(true);
//        resultTextArea.setWrapStyleWord(true);
//        JScrollPane scrollPane = new JScrollPane(resultTextArea);
//        add(scrollPane);

        generate.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent evt) {
                System.out.println("Generate button clicked.");
                getRecipeController.execute();
                getRecipeViewModel.firePropertyChange();
                List<Map<String, List<String>>> recipes = getRecipeViewModel.getRecipes();
                showRecipes(recipes);
            }
        });

        MainMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (evt.getSource().equals(MainMenu)) {
                    // Assuming the viewName for GetRecipeView is "get recipe"
                }
            }
        });
    }

    private class RecipePanel extends JPanel {
        private String recipe;
        private List<String> ingredients;
        private JTextArea resultTextArea;
        private final JButton selectButton;

        public RecipePanel(String recipe, List<String> ingredients) {
            super();
            this.recipe = recipe;
            this.ingredients = ingredients;

            this.resultTextArea = new JTextArea(recipe);
            this.resultTextArea.setLineWrap(true);
            this.selectButton = new JButton("Make a Shopping List");

            JScrollPane scrollPane = new JScrollPane(resultTextArea);
            scrollPane.setPreferredSize(new Dimension(600, 90));
            this.add(scrollPane);

            this.add(selectButton);
            this.setBackground(Color.WHITE);

            selectButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    System.out.println("Make Shopping List button clicked.");
                    getShoppingListController.execute(ingredients);
                }
            });
        }
    }

    private void showRecipes(List<Map<String, List<String>>> recipes) {
//        for (String recipe : recipes) {
//            resultTextArea.append(recipe + "\n");
//        }

        recipesPanel.removeAll();
        recipesPanel.setPreferredSize(new Dimension(600, 150 * recipes.size()));

        for (Map<String, List<String>> recipe: recipes) {
            StringBuilder display = new StringBuilder();

            List<String> title = recipe.get("Name");
            String name = title.get(0);

            display.append("Name: " + name + "\n");
            for (String info: recipe.keySet()) {
                if (info.equals("Name")) { continue; }
                display.append(info + ": ");

                if (info.equals("Instructions")) {
                    for (String step: recipe.get(info)) {
                        display.append(step + "\n");
                    }
                    continue;
                }
                for (String item: recipe.get(info)) {
                    String[] temp = item.split(":");
                    display.append(temp[0] + ": " + temp[1] + "\n");
                }
            }

            RecipePanel recipePanel = new RecipePanel(display.toString(), recipe.get("Ingredients"));
            recipePanel.setPreferredSize(new Dimension(600, 150));
            recipesPanel.add(recipePanel);
        }

        recipesPanel.repaint();
        recipesPanel.revalidate();
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        System.out.println("Cancel not implemented yet.");
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getPropertyName().equals("recipeState")) {
            GetRecipeState state = (GetRecipeState) evt.getNewValue();
            if (state.getError() != null)
                JOptionPane.showMessageDialog(this, "Recipes: Will finalize when API works");
        }
        else {
            GetShoppingListState state = (GetShoppingListState) evt.getNewValue();
            if (state.getError() == null) {
                System.out.println(state.getShoppingList());
                ShoppingListGenerator.writeShoppingListToFile(state.getShoppingList());
            }
            else {
                JOptionPane.showMessageDialog(this, state.getError());
            }

        }

    }

    private static class ShoppingListGenerator {
        private static FileWriter shoppingListFile;

        public static void setPath(String filePath) {
            try {
                shoppingListFile = new FileWriter(filePath);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public static void writeShoppingListToFile(List<String> shoppingList) {
            try {
                BufferedWriter bufferedWriter = new BufferedWriter(shoppingListFile);
                bufferedWriter.write("**Shopping List**");
                bufferedWriter.newLine();
                bufferedWriter.newLine();
                bufferedWriter.write("|Name    | Amount |");
                bufferedWriter.newLine();
                bufferedWriter.write("|-----| ---:|");
                bufferedWriter.newLine();
                for (String foodItem : shoppingList) {
                    String[] foodItemData = foodItem.split(":");
                    bufferedWriter.write("| " + foodItemData[0] + " | " + foodItemData[1] + " |");
                    bufferedWriter.newLine();
                }
                bufferedWriter.close();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
